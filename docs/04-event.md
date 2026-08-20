# Event

*O alfabeto de ações da tela*

`Event` representa **o alfabeto de ações** que a tela sabe reagir — normalmente coisas que o usuário fez (cliques, texto digitado), mas também podem ser eventos do sistema.

## Por que `sealed interface`?

Um `sealed interface` (ou `sealed class`) fecha o conjunto de tipos possíveis: o compilador sabe exatamente quais são todas as variantes. Isso faz o `when (event) { ... }` dentro da ViewModel ser **exaustivo** — se alguém adicionar uma nova variante de `Event` e esquecer de tratar no `when`, o projeto **não compila**. Isso vira uma rede de segurança automática contra "esqueci de implementar esse botão".

*Ruim — interface aberta*

```kotlin
interface AnrEvent
object OnTriggerSleep : AnrEvent
object OnFixSleep : AnrEvent
// qualquer arquivo, em qualquer módulo, pode criar mais um "AnrEvent" --
// o compilador não tem como saber a lista completa

fun onEvent(event: AnrEvent) {
    when (event) {
        OnTriggerSleep -> triggerSleep()
        // esqueceu o OnFixSleep aqui -- compila normalmente, bug silencioso
        else -> Unit
    }
}
```

*Bom — sealed interface*

```kotlin
sealed interface AnrEvent {
    data object OnTriggerSleep : AnrEvent
    data object OnFixSleep : AnrEvent
    // só este arquivo pode declarar variantes de AnrEvent
}

fun onEvent(event: AnrEvent) {
    when (event) {
        AnrEvent.OnTriggerSleep -> triggerSleep()
        // erro de compilação: "when" não é exaustivo,
        // falta tratar OnFixSleep e as outras variantes
    }
}
```

Esquecer uma variante vira erro de compilação, não bug em produção.

## Exemplo real: `AnrEvent`

[`AnrState.kt`](../feature/anr/src/main/kotlin/io/lb/bleandlistingopt/feature/anr/AnrState.kt):

```kotlin
sealed interface AnrEvent {
    data object OnTriggerSleep : AnrEvent
    data object OnFixSleep : AnrEvent
    data object OnTriggerCpuLoop : AnrEvent
    data object OnFixCpuLoop : AnrEvent
    data object OnTriggerDiskRead : AnrEvent
    data object OnFixDiskRead : AnrEvent
    data object OnTriggerDeadlock : AnrEvent
    data object OnFixDeadlock : AnrEvent
}
```

Note o padrão de nomenclatura: `data object` para eventos sem dados (um clique simples) e, quando um evento carrega informação, `data class` — como em `BluetoothEvent.OnDeviceClick(val address: String)` (veja [`BluetoothState.kt`](../feature/bluetooth/presentation/src/main/kotlin/io/lb/bleandlistingopt/feature/bluetooth/presentation/BluetoothState.kt)). O nome sempre começa com `On` + o que aconteceu, nunca com o que a tela deve *fazer* — quem decide o que fazer é a ViewModel, não quem dispara o evento.

## O único ponto de entrada: `onEvent`

Trecho real de [`AnrViewModel.kt`](../feature/anr/src/main/kotlin/io/lb/bleandlistingopt/feature/anr/AnrViewModel.kt):

```kotlin
fun onEvent(event: AnrEvent) {
    when (event) {
        AnrEvent.OnTriggerSleep -> triggerSleep()
        AnrEvent.OnFixSleep -> fixSleep()
        AnrEvent.OnTriggerCpuLoop -> triggerCpuLoop()
        AnrEvent.OnFixCpuLoop -> fixCpuLoop()
        AnrEvent.OnTriggerDiskRead -> triggerDiskRead()
        AnrEvent.OnFixDiskRead -> fixDiskRead()
        AnrEvent.OnTriggerDeadlock -> triggerDeadlock()
        AnrEvent.OnFixDeadlock -> fixDeadlock()
    }
}
```

Toda a UI dessa tela, não importa quantos botões tenha, chama exatamente esse método. Isso significa que dá pra entender **tudo** que uma tela pode fazer só lendo essa função — sem precisar caçar `onClick`s espalhados pelo código Compose ou pela Activity. Os 4 pares de `Event` acima (trigger/fix) são explicados em detalhe, com o código completo de cada handler, em [ANR Lab: os 4 pares lado a lado](./07-anr-lab-pares.md).

---

[↑ Índice](./index.md) · [Anterior: State](./03-state.md) · [Próximo: Effect →](./05-effect.md)
