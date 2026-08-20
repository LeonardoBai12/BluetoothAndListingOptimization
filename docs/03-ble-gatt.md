---
layout: default
title: "Bluetooth (GATT)"
nav_order: 4
---

# Bluetooth — GATT, MTU e a fila de operações serializada

*O gotcha clássico de BLE em nível sênior*

## Vocabulário

- **GATT** (Generic Attribute Profile): o protocolo que dispositivos BLE falam depois de conectados. `BluetoothGatt` é o handle Android para isso, do lado cliente.
- **Service**: um grupo nomeado de valores relacionados que um periférico expõe (ex: "Heart Rate"), identificado por um UUID.
- **Characteristic**: um valor legível/gravável/notificável dentro de um service (ex: "Heart Rate Measurement"), também identificado por UUID.
- **Descriptor**: metadado ligado a uma characteristic. O que este projeto grava, em `enableNotifications`, é o descriptor "Client Characteristic Configuration" — gravar o valor mágico `BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE` nele é literalmente como um cliente BLE diz ao periférico "comece a me mandar atualizações dessa characteristic".
- **MTU** (Maximum Transmission Unit): o maior número de bytes que um pacote BLE consegue carregar numa única transmissão. Todo valor de characteristic maior que o MTU disponível precisa ser fragmentado em múltiplos pacotes, o que significa mais idas e vindas de rádio para transferir o mesmo dado. Detalhes de como esse número é definido (não é fixo, é "negociado") na seção [O que "negociar MTU" quer dizer, de verdade](#o-que-negociar-mtu-quer-dizer-de-verdade) mais abaixo.

Definições completas em [`BleGattClient.kt`](https://github.com/LeonardoBai12/BluetoothAndListingOptimization/blob/main/feature/bluetooth/data/src/main/kotlin/io/lb/bleandlistingopt/feature/bluetooth/data/real/BleGattClient.kt).

## O gotcha: só uma operação GATT por vez

Toda chamada de `BluetoothGatt` (`discoverServices`, `requestMtu`, `readCharacteristic`, `writeDescriptor`, ...) é assíncrona — ela retorna `true`/`false` na hora ("o pedido entrou na fila do rádio?") e o resultado de verdade chega depois, num método de `BluetoothGattCallback`. Só que a pilha Bluetooth por baixo só processa **uma** operação GATT por vez, por conexão. Disparar uma segunda chamada antes do callback da primeira ter chegado faz a segunda ser descartada, retornar `false`, ou — pior — corromper silenciosamente o estado interno da conexão, porque a pilha não tem onde enfileirar.

A correção, [`GattOperationQueue.kt`](https://github.com/LeonardoBai12/BluetoothAndListingOptimization/blob/main/feature/bluetooth/data/src/main/kotlin/io/lb/bleandlistingopt/feature/bluetooth/data/real/GattOperationQueue.kt):

```kotlin
class GattOperationQueue {
    private val mutex = Mutex()

    suspend fun <T> enqueue(operation: suspend () -> T): T = mutex.withLock { operation() }
}
```

Toda operação GATT é envolvida numa função suspend que: (1) faz a chamada, (2) suspende num `CompletableDeferred` que só o método correspondente de `BluetoothGattCallback` completa, e (3) roda dentro de `withLock`. Como a operação encapsulada não *retorna* até seu callback disparar, o mutex também não é liberado até lá — então a próxima operação enfileirada fisicamente não pode começar antes da anterior terminar. Um `Mutex`, uma operação em voo, exatamente batendo com o que o rádio consegue fazer de verdade.

## Como fica na prática

Em [`BleGattClient.kt`](https://github.com/LeonardoBai12/BluetoothAndListingOptimization/blob/main/feature/bluetooth/data/src/main/kotlin/io/lb/bleandlistingopt/feature/bluetooth/data/real/BleGattClient.kt), o pedido de leitura:

```kotlin
suspend fun readCharacteristic(serviceUuid: UUID, characteristicUuid: UUID): Resource<CharacteristicValue> =
    operationQueue.enqueue {
        val characteristic = gatt?.getService(serviceUuid)?.getCharacteristic(characteristicUuid)
            ?: return@enqueue Resource.Error("Characteristic not found")

        val deferred = CompletableDeferred<Resource<CharacteristicValue>>()
        pendingRead = deferred
        val started = gatt?.readCharacteristic(characteristic) ?: false
        if (!started) return@enqueue Resource.Error("readCharacteristic() rejected -- radio busy or disconnected")
        deferred.await()
    }
```

E o callback que resolve o `deferred` pendente:

```kotlin
override fun onCharacteristicRead(
    connectedGatt: BluetoothGatt,
    characteristic: BluetoothGattCharacteristic,
    value: ByteArray,
    status: Int,
) {
    val result = if (status == BluetoothGatt.GATT_SUCCESS) {
        Resource.Success(CharacteristicValue(characteristic.uuid.toString(), value))
    } else {
        Resource.Error("Read failed, status=$status")
    }
    pendingRead?.complete(result)
}
```

Note o nome do parâmetro do callback: `connectedGatt`, não a letra solta `g` que a documentação da Android costuma usar — nenhum desses callbacks precisa do campo `gatt` da classe, só da instância de conexão que o sistema acabou de devolver para aquele evento específico.

## Conectar, descobrir serviços, negociar MTU — nessa ordem

```kotlin
suspend fun connect(): Resource<Unit> {
    _connectionState.value = ConnectionState.Connecting
    val deferred = CompletableDeferred<Resource<Unit>>()
    pendingConnection = deferred
    gatt = device.connectGatt(context, false, callback)

    val connectResult = deferred.await()
    if (connectResult !is Resource.Success) return connectResult

    // connectGatt() em si não é enfileirado (não tem contra o que
    // serializar ainda -- é o que cria a conexão que essas operações
    // precisam), mas tudo a partir daqui é.
    val discovered = operationQueue.enqueue {
        val serviceDiscoveryResult = CompletableDeferred<Boolean>()
        pendingServiceDiscovery = serviceDiscoveryResult
        gatt?.discoverServices()
        serviceDiscoveryResult.await()
    }
    if (!discovered) return Resource.Error("Service discovery failed")

    val negotiatedMtu = operationQueue.enqueue {
        val mtuResult = CompletableDeferred<Int>()
        pendingMtu = mtuResult
        gatt?.requestMtu(TARGET_MTU)
        mtuResult.await()
    }

    _connectionState.value = ConnectionState.Connected(mtu = negotiatedMtu)
    return Resource.Success(Unit)
}
```

`connectGatt()` cria a conexão em si e não passa pela fila (nada existe ainda para serializar contra); a descoberta de serviços e a negociação de MTU acontecem depois, cada uma pela fila, uma de cada vez.

## O que "negociar MTU" quer dizer, de verdade

Toda conexão BLE começa com um MTU de 23 bytes (20 úteis, depois de descontar o cabeçalho do protocolo ATT) — um valor pequeno de propósito, para garantir que qualquer dispositivo, mesmo o mais limitado, consiga participar de uma conexão. Esse valor pequeno é ruim para transferências maiores (a leitura de uma characteristic de 100 bytes, por exemplo, precisaria de vários pacotes em sequência em vez de um só), então o app pede um MTU maior via `requestMtu()`. A palavra "negociar" existe porque **pedir não é o mesmo que receber**: o outro lado da conexão (o periférico, ou às vezes o próprio controlador Bluetooth do Android) pode não suportar o valor pedido e conceder um menor — o valor final só é conhecido quando o callback `onMtuChanged` dispara, e é ele, não o valor pedido, que o código deve usar dali para frente.

Isso aparece direto no callback, em [`BleGattClient.kt`](https://github.com/LeonardoBai12/BluetoothAndListingOptimization/blob/main/feature/bluetooth/data/src/main/kotlin/io/lb/bleandlistingopt/feature/bluetooth/data/real/BleGattClient.kt):

```kotlin
override fun onMtuChanged(connectedGatt: BluetoothGatt, mtu: Int, status: Int) {
    pendingMtu?.complete(if (status == BluetoothGatt.GATT_SUCCESS) mtu else DEFAULT_ATT_MTU)
}
```

`requestMtu(TARGET_MTU)` pede 247 bytes (o teto prático em BLE 4.2+), mas o `mtu` que chega aqui é o valor que **realmente** foi acordado — pode ser 247, pode ser menor, dependendo do que o periférico aceita. Se `status` não for sucesso, o código nem tenta adivinhar um meio-termo: volta para o padrão seguro de 23 bytes (`DEFAULT_ATT_MTU`), porque assumir um valor maior sem confirmação faria transmissões subsequentes falharem. É exatamente esse valor — o que veio no callback, não o que foi pedido — que vira `ConnectionState.Connected(mtu = negotiatedMtu)` em `connect()`.

## Testar sem hardware BLE

Emuladores Android não têm rádio Bluetooth real — `BluetoothAdapter.getBluetoothLeScanner()` retorna `null` ou um scanner que não encontra nada. Teste em hardware real precisa de um dispositivo físico e um periférico BLE por perto — o app [nRF Connect](https://www.nordicsemi.com/Products/Development-tools/nRF-Connect-for-mobile) em modo periférico, simulando o profile padrão de Heart Rate, é o mais simples de usar (é literalmente o profile que [`BluetoothViewModel.kt`](https://github.com/LeonardoBai12/BluetoothAndListingOptimization/blob/main/feature/bluetooth/presentation/src/main/kotlin/io/lb/bleandlistingopt/feature/bluetooth/presentation/BluetoothViewModel.kt) usa).

---

[↑ Índice](./) · [Anterior: Compose (LazyColumn)](./02-compose-lazycolumn/) · [Próximo: ANR Lab →](./04-anr-lab/)
