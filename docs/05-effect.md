# Effect

*O que acontece uma vez só*

`Effect` é para o que **acontece uma vez só** e não é, de verdade, "estado da tela" — mostrar uma mensagem de erro, navegar para outra tela, disparar um Toast.

## A diferença fundamental entre State e Effect

| | State | Effect |
| --- | --- | --- |
| Representa | "Como a tela está agora" | "Isso precisa acontecer agora, uma vez" |
| Sobrevive a rotação? | Sim, deve continuar valendo | Não, não deve disparar de novo |
| Tipo Kotlin | `StateFlow` — sempre guarda e reentrega o último valor | `SharedFlow` — não guarda valor nenhum por padrão, só quem estiver ouvindo no momento da emissão recebe |

## Exemplo real: `BluetoothEffect`

[`BluetoothState.kt`](../feature/bluetooth/presentation/src/main/kotlin/io/lb/bleandlistingopt/feature/bluetooth/presentation/BluetoothState.kt):

```kotlin
sealed interface BluetoothEffect {
    data class ShowError(val message: String) : BluetoothEffect
}
```

E como a ViewModel emite (trecho real de [`BluetoothViewModel.kt`](../feature/bluetooth/presentation/src/main/kotlin/io/lb/bleandlistingopt/feature/bluetooth/presentation/BluetoothViewModel.kt)):

```kotlin
private val _effects = MutableSharedFlow<BluetoothEffect>()
val effects: SharedFlow<BluetoothEffect> = _effects.asSharedFlow()

// dentro de connect(), se a conexão falhar:
_effects.emit(BluetoothEffect.ShowError(result.message ?: "Connection failed"))
```

## Armadilha clássica: colocar a mensagem de erro dentro do State

*Ruim — erro como campo do State*

```kotlin
data class BluetoothState(
    // ...
    val errorMessage: String? = null,
)

// ao falhar a conexão:
_state.update { it.copy(errorMessage = "Connection failed") }
```

Gira a tela → State é reemitido com o valor mais recente (é isso que `StateFlow` faz) → o Snackbar de erro aparece de novo, mesmo já tendo sido visto.

*Bom — erro como Effect*

```kotlin
sealed interface BluetoothEffect {
    data class ShowError(val message: String) : BluetoothEffect
}

// ao falhar a conexão:
_effects.emit(BluetoothEffect.ShowError("Connection failed"))
```

Gira a tela → nada é reemitido, porque `SharedFlow` não guarda valor nenhum por padrão. O erro só aparece uma vez, exatamente quando aconteceu.

---

[↑ Índice](./index.md) · [Anterior: Event](./04-event.md) · [Próximo: Como as três peças se conectam →](./06-fluxo-completo.md)
