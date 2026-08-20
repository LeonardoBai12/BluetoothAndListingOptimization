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

## Por que "Heart Rate", especificamente

Esse projeto lê e assina notificações de um service/characteristic bem específico em vários lugares — vale explicar o que é, já que aparece sem contexto no código. "Heart Rate" é um **profile GATT padronizado pelo Bluetooth SIG** (a organização dona do padrão Bluetooth): um conjunto fixo de service + characteristics que qualquer monitor de frequência cardíaca do mercado (relógio, cinta de peito, etc.) implementa do mesmo jeito, com os mesmos UUIDs — `0000180d-...` para o service "Heart Rate" e `00002a37-...` para a characteristic "Heart Rate Measurement", exatamente os valores em [`BluetoothViewModel.kt`](https://github.com/LeonardoBai12/BluetoothAndListingOptimization/blob/main/feature/bluetooth/presentation/src/main/kotlin/io/lb/bleandlistingopt/feature/bluetooth/presentation/BluetoothViewModel.kt). Não tem nada a ver com o app medir batimento cardíaco de verdade — foi escolhido porque é um dos profiles BLE mais usados como "hello world" da área, e por isso é o profile que simuladores de periférico (como o nRF Connect, na seção [Testar sem hardware BLE](#testar-sem-hardware-ble) abaixo) já vêm prontos para emular, sem precisar escrever um servidor GATT customizado só para testar este projeto.

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

## Escrever numa characteristic

Ler e escrever seguem o mesmo padrão (`CompletableDeferred` + fila), mas nem toda characteristic aceita as duas operações — o profile Heart Rate usado neste projeto só tem uma que aceita escrita: "Heart Rate Control Point" (`0x2A39`), que reseta o contador de energia gasta acumulada pelo periférico quando recebe o byte `0x01`. É esse o botão "Reset energy expended" na tela de Bluetooth.

```kotlin
@Suppress("DEPRECATION") // o overload de 3 argumentos writeCharacteristic(characteristic, value, writeType) pede API 33; minSdk aqui é 24
suspend fun writeCharacteristic(serviceUuid: UUID, characteristicUuid: UUID, value: ByteArray): Resource<Unit> =
    operationQueue.enqueue {
        val characteristic = gatt?.getService(serviceUuid)?.getCharacteristic(characteristicUuid)
            ?: return@enqueue Resource.Error("Characteristic not found")

        val deferred = CompletableDeferred<Boolean>()
        pendingWrite = deferred
        characteristic.value = value
        val started = gatt?.writeCharacteristic(characteristic) ?: false
        if (!started) return@enqueue Resource.Error("writeCharacteristic() rejected -- radio busy or disconnected")
        if (deferred.await()) Resource.Success(Unit) else Resource.Error("Write failed")
    }
```

A forma é praticamente idêntica à leitura: mesma fila, mesmo `CompletableDeferred`, mesma checagem de retorno imediato (`started`) separada do resultado real (`deferred.await()`, resolvido só quando `onCharacteristicWrite` disparar). A diferença central é onde o dado viaja: na leitura, o valor vem *do* periférico *para* o app, dentro do callback; na escrita, o valor sai do app **antes** da chamada (`characteristic.value = value`), e o callback só confirma se aquele valor chegou (`status == GATT_SUCCESS`) — ele não devolve o dado de volta.

## GATT diz *onde* escrever, nunca *o que os bytes significam*

`writeCharacteristic` é como se manda um "comando" para um dispositivo BLE — não existe uma API separada para "comandos", é a mesma operação de escrita de sempre, só que o valor escrito é interpretado pelo periférico como uma instrução em vez de um dado solto. O que o GATT garante é só a parte estrutural: qual UUID de service, qual UUID de characteristic, e se ela aceita escrita (a flag `PROPERTY_WRITE`, visível ao descobrir os serviços). Ele **não** garante nada sobre o significado dos bytes — isso vem sempre de uma especificação escrita em algum lugar, fora do protocolo em si, e existem dois casos bem diferentes na prática.

**Characteristic padronizada pelo Bluetooth SIG** — o caso de Heart Rate Control Point usado neste projeto. O UUID (`0x2A39`) e o formato do byte (`0x01` = "resetar energia gasta") são públicos, documentados pelo SIG, e valem para o profile inteiro — qualquer fabricante que implemente Heart Rate usa o mesmo UUID com o mesmo significado. Dá para escrever o código sem nunca ter o dispositivo físico na mão, só lendo a especificação.

**Characteristic proprietária de um fabricante** — o caso mais comum em produtos reais (uma fechadura inteligente, uma fita de LED, um wearable com funções além do que qualquer profile padrão cobre). Aqui o fabricante define seus próprios UUIDs e seu próprio formato de byte, geralmente num SDK ou datasheet que não é público — sem esse documento, o GATT sozinho não dá informação nenhuma sobre o que escrever. É comum, inclusive, o fabricante implementar só **duas** characteristics customizadas (uma para escrever, outra que notifica de volta) e tunelar o protocolo inteiro dele por cima delas — um padrão tão frequente que tem nome, "Nordic UART Service" (NUS), mesmo quando não é da Nordic: na prática, é um canal serial genérico dentro do BLE, e o "protocolo de verdade" mora inteiramente dentro dos bytes que trafegam por ele, invisível ao GATT.

## Lidando com um dispositivo real sem a documentação do fabricante

Quando o protocolo é proprietário e a documentação não está disponível, o caminho prático é:

1. **Explorar a tabela GATT primeiro, sem tentar escrever nada.** O [nRF Connect](https://www.nordicsemi.com/Products/Development-tools/nRF-Connect-for-mobile) conecta em qualquer dispositivo BLE e lista todos os services/characteristics dele, com as properties de cada uma (`READ`/`WRITE`/`WRITE_NO_RESPONSE`/`NOTIFY`/`INDICATE`) — isso já revela a "forma" do protocolo (quantas characteristics existem, quais aceitam escrita, quais notificam) sem precisar de nenhuma documentação.
2. **Observar o app oficial do fabricante em ação.** Se existe um app que já controla o dispositivo, capturar um HCI snoop log (`adb bugreport`, ou a opção "Bluetooth HCI snoop log" nas opções de desenvolvedor do Android) enquanto usa esse app registra cada pacote GATT trocado. Abrindo esse log num analisador de protocolo (Wireshark entende o formato), dá para ver exatamente quais bytes o app oficial escreve em qual characteristic para produzir qual efeito — engenharia reversa do protocolo por observação, sem precisar de nenhum acesso ao código-fonte do fabricante.
3. **Testar hipóteses manualmente.** Com um palpite de formato (um byte de comando, um valor de parâmetro depois), o próprio nRF Connect permite escrever bytes arbitrários numa characteristic e observar a reação do dispositivo — um ciclo de tentativa e erro guiado pelo que já foi aprendido nos passos anteriores.

Nada disso muda o código deste projeto: a fila serializada, o `CompletableDeferred` por operação, e `writeCharacteristic` continuam sendo exatamente o mecanismo certo para enviar qualquer comando, de qualquer protocolo — a única coisa que muda de um dispositivo padronizado para um proprietário é **de onde vêm** o UUID e o formato do byte, não como a escrita é feita.

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

## O segundo argumento de `connectGatt`, `autoConnect`

`device.connectGatt(context, false, callback)` — esse `false` é uma decisão real, fácil de inverter sem perceber, e o Android não avisa quando você escolhe o valor errado. `autoConnect = false` (o que este projeto usa) é uma conexão **direta**: rápida, mas falha na hora se o dispositivo não estiver alcançável *agora*, no exato momento da chamada. `autoConnect = true` é uma conexão em **segundo plano**: o sistema operacional guarda o pedido e fica tentando reconectar sozinho, silenciosamente, sempre que aquele dispositivo entrar no alcance — só que a primeira conexão bem-sucedida demora muito mais para acontecer, porque ela passa por um caminho interno do stack Bluetooth otimizado para espera longa, não para resposta rápida.

A escolha certa depende do que o usuário acabou de fazer: `false` é certo aqui porque o fluxo é "o usuário tocou em Connect num dispositivo que a tela literalmente acabou de ver no scan" — está por perto, agora. `true` seria certo para "reconectar automaticamente com um dispositivo já pareado sempre que ele aparecer", como uma pulseira fitness que entra e sai de alcance ao longo do dia sem o usuário abrir o app de novo. Trocar os dois deixa sintomas confusos: usar `true` para um connect que devia ser imediato faz a conexão parecer "lenta" ou "travada" sem motivo aparente; usar `false` para reconexão automática nunca reconecta sozinho, exigindo o app chamar `connect()` de novo na mão toda vez.

## O que "negociar MTU" quer dizer, de verdade

Toda conexão BLE começa com um MTU de 23 bytes (20 úteis, depois de descontar o cabeçalho do protocolo ATT) — um valor pequeno de propósito, para garantir que qualquer dispositivo, mesmo o mais limitado, consiga participar de uma conexão. Esse valor pequeno é ruim para transferências maiores (a leitura de uma characteristic de 100 bytes, por exemplo, precisaria de vários pacotes em sequência em vez de um só), então o app pede um MTU maior via `requestMtu()`. A palavra "negociar" existe porque **pedir não é o mesmo que receber**: o outro lado da conexão (o periférico, ou às vezes o próprio controlador Bluetooth do Android) pode não suportar o valor pedido e conceder um menor — o valor final só é conhecido quando o callback `onMtuChanged` dispara, e é ele, não o valor pedido, que o código deve usar dali para frente.

Isso aparece direto no callback, em [`BleGattClient.kt`](https://github.com/LeonardoBai12/BluetoothAndListingOptimization/blob/main/feature/bluetooth/data/src/main/kotlin/io/lb/bleandlistingopt/feature/bluetooth/data/real/BleGattClient.kt):

```kotlin
override fun onMtuChanged(connectedGatt: BluetoothGatt, mtu: Int, status: Int) {
    pendingMtu?.complete(if (status == BluetoothGatt.GATT_SUCCESS) mtu else DEFAULT_ATT_MTU)
}
```

`requestMtu(TARGET_MTU)` pede 247 bytes (o teto prático em BLE 4.2+), mas o `mtu` que chega aqui é o valor que **realmente** foi acordado — pode ser 247, pode ser menor, dependendo do que o periférico aceita. Se `status` não for sucesso, o código nem tenta adivinhar um meio-termo: volta para o padrão seguro de 23 bytes (`DEFAULT_ATT_MTU`), porque assumir um valor maior sem confirmação faria transmissões subsequentes falharem. É exatamente esse valor — o que veio no callback, não o que foi pedido — que vira `ConnectionState.Connected(mtu = negotiatedMtu)` em `connect()`.

## Escanear sem filtro mostra qualquer coisa BLE por perto

*Problema* — o scanner original deste projeto chamava `startScan(null, settings, callback)`. Um scan sem `ScanFilter` devolve **todo** anúncio BLE que o rádio conseguir captar por perto — a TV, a impressora do escritório, um fone de ouvido de outra pessoa, qualquer coisa. O app deixa o usuário tocar em "Connect" em qualquer um desses, mas os botões de leitura/notificação estão fixos no service UUID de Heart Rate — conectar numa TV e apertar "Read" só resulta em erro, porque a TV nunca teve esse service para começo de conversa. A lista virava ruído sem relação nenhuma com o que o app realmente sabe fazer.

*Solução* — [`BleScanner.kt`](https://github.com/LeonardoBai12/BluetoothAndListingOptimization/blob/main/feature/bluetooth/data/src/main/kotlin/io/lb/bleandlistingopt/feature/bluetooth/data/real/BleScanner.kt):

```kotlin
val filters = serviceUuid?.let {
    listOf(ScanFilter.Builder().setServiceUuid(ParcelUuid(UUID.fromString(it))).build())
}
scanner.startScan(filters, settings, callback)
```

E [`BluetoothViewModel.kt`](https://github.com/LeonardoBai12/BluetoothAndListingOptimization/blob/main/feature/bluetooth/presentation/src/main/kotlin/io/lb/bleandlistingopt/feature/bluetooth/presentation/BluetoothViewModel.kt) decide o UUID a partir de um `Switch` na própria tela (`state.scanFilterEnabled`), não de um valor fixo:

```kotlin
val filterUuid = if (_state.value.scanFilterEnabled) HEART_RATE_SERVICE_UUID else null
scanForDevices(serviceUuid = filterUuid).collect { device -> ... }
```

Um `ScanFilter` com `setServiceUuid` não filtra no app depois do scan — ele instrui o **rádio** a descartar anúncios que não declarem aquele service UUID no próprio pacote de advertising, antes mesmo de chegar no callback. Isso tem duas vantagens sobre filtrar a lista na UI depois: menos trabalho para o rádio (e menos bateria gasta processando anúncios que seriam descartados de qualquer jeito) e uma lista que só mostra dispositivos com os quais o app consegue mesmo interagir. O `Switch` "Filtro: Heart Rate" na tela de scan existe justamente para isso ser visível: ligado, mostra só dispositivos com o service de Heart Rate anunciado; desligado, mostra qualquer BLE por perto — dá para comparar as duas listas lado a lado, no mesmo lugar, sem hardware nenhum além do próprio celular.

## Testar sem hardware BLE

Emuladores Android não têm rádio Bluetooth real — `BluetoothAdapter.getBluetoothLeScanner()` retorna `null` ou um scanner que não encontra nada. Teste em hardware real precisa de um dispositivo físico e um periférico BLE por perto — o app [nRF Connect](https://www.nordicsemi.com/Products/Development-tools/nRF-Connect-for-mobile) em modo periférico, simulando o profile padrão de Heart Rate, é o mais simples de usar (é literalmente o profile que [`BluetoothViewModel.kt`](https://github.com/LeonardoBai12/BluetoothAndListingOptimization/blob/main/feature/bluetooth/presentation/src/main/kotlin/io/lb/bleandlistingopt/feature/bluetooth/presentation/BluetoothViewModel.kt) usa).

---

[↑ Índice](./) · [Anterior: Compose (LazyColumn)](./02-compose-lazycolumn/) · [Próximo: ANR Lab →](./04-anr-lab/)
