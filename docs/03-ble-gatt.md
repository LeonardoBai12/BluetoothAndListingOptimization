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
- **MTU** (Maximum Transmission Unit): o maior número de bytes que um pacote BLE carrega. O padrão é 23 bytes (20 utilizáveis); por isso o `connect()` negocia um MTU maior antes de considerar a conexão pronta.

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

## Testar sem hardware BLE

Emuladores Android não têm rádio Bluetooth real — `BluetoothAdapter.getBluetoothLeScanner()` retorna `null` ou um scanner que não encontra nada. Teste em hardware real precisa de um dispositivo físico e um periférico BLE por perto — o app [nRF Connect](https://www.nordicsemi.com/Products/Development-tools/nRF-Connect-for-mobile) em modo periférico, simulando o profile padrão de Heart Rate, é o mais simples de usar (é literalmente o profile que [`BluetoothViewModel.kt`](https://github.com/LeonardoBai12/BluetoothAndListingOptimization/blob/main/feature/bluetooth/presentation/src/main/kotlin/io/lb/bleandlistingopt/feature/bluetooth/presentation/BluetoothViewModel.kt) usa).

---

[↑ Índice](./) · [Anterior: Compose (LazyColumn)](./02-compose-lazycolumn/) · [Próximo: ANR Lab →](./04-anr-lab/)
