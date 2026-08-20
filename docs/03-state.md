# State

*A única fonte de verdade da tela*

O `State` é a **única fonte de verdade** da tela: um snapshot imutável de tudo que a UI precisa para se desenhar num determinado momento.

## Características de um bom State

- **É uma `data class`** — imutável, com `equals`/`copy` de graça, o que permite comparar dois states e saber exatamente o que mudou.
- **Tem valores padrão** — o estado inicial da tela é só `MinhaState()`, sem precisar montar um objeto "vazio" na mão em vários lugares.
- **É completo** — a UI nunca deveria precisar de uma variável fora do State para saber o que desenhar. Se a UI tem uma `var` própria controlando alguma coisa visível, isso é um sinal de que faltou um campo no State.
- **É exposto como `StateFlow`** — sempre tem um valor atual (`.value`), e quem observa recebe automaticamente o valor mais recente ao começar a observar (diferente de um `SharedFlow`, que só entrega o que for emitido depois que alguém começar a ouvir — veja [Effect](./05-effect.md)).

## Exemplo real: `BluetoothState`

[`BluetoothState.kt`](../feature/bluetooth/presentation/src/main/kotlin/io/lb/bleandlistingopt/feature/bluetooth/presentation/BluetoothState.kt):

```kotlin
data class BluetoothState(
    val devices: List<BleDevice> = emptyList(),
    val isScanning: Boolean = false,
    val selectedAddress: String? = null,
    val connectionState: ConnectionState = ConnectionState.Disconnected,
    val isObservingNotifications: Boolean = false,
    val lastReadValue: String? = null,
)
```

Repare que **tudo** que a tela de Bluetooth precisa mostrar está aqui: a lista de dispositivos encontrados, se está escaneando, qual dispositivo está selecionado, o estado da conexão, se está ouvindo notificações e o último valor lido. A tela Compose ([`BluetoothScreen.kt`](../feature/bluetooth/presentation/src/main/kotlin/io/lb/bleandlistingopt/feature/bluetooth/presentation/BluetoothScreen.kt)) só faz `collectAsStateWithLifecycle()` nesse state e desenha em cima — nenhuma outra fonte de verdade.

> **Por que não vários `MutableStateFlow` soltos?** Dá pra imaginar uma versão com `devices`, `isScanning` etc. cada um em seu próprio `StateFlow` separado. O problema: a UI teria que observar vários flows ao mesmo tempo e combiná-los na mão, e não existiria um "snapshot" único e consistente — dois campos observados separadamente podem, por um instante, refletir dois momentos diferentes. Um `State` único resolve isso: é sempre uma foto completa e consistente.

## O anti-padrão do outro lado: `List` vs `ImmutableList`

O par de states do módulo de listagem em Compose mostra outra dimensão de "o que faz um bom State" — a estabilidade do tipo para o compilador do Compose:

*Anti-padrão* — [`UnoptimizedListState.kt`](../feature/listing/compose/src/main/kotlin/io/lb/bleandlistingopt/feature/listing/compose/unoptimized/UnoptimizedListState.kt):

```kotlin
data class UnoptimizedListState(
    val items: List<ListingItem> = emptyList(), // plain List: não é
    val query: String = "",                     // stability-inferable
)
```

*Corrigido* — [`OptimizedListState.kt`](../feature/listing/compose/src/main/kotlin/io/lb/bleandlistingopt/feature/listing/compose/optimized/OptimizedListState.kt):

```kotlin
data class OptimizedListState(
    val items: ImmutableList<ListingItem> = persistentListOf(), // stable
    val query: String = "",
)
```

`List` não é um tipo que o compilador do Compose consegue provar estável — poderia ser uma `MutableList` por baixo, mutada depois de passada adiante. `ImmutableList` (de `kotlinx.collections.immutable`) é. Essa diferença de tipo é o motivo que justifica os dois states serem classes separadas — ver [SRP](./02-srp.md).

---

[↑ Índice](./index.md) · [Anterior: SRP](./02-srp.md) · [Próximo: Event →](./04-event.md)
