# MVI e UDF

*O que é o padrão e por que este projeto usa*

MVI (Model-View-Intent) é um jeito de organizar a lógica de uma tela em três peças bem separadas, para que a tela nunca fique em um estado inconsistente e para que toda mudança tenha uma origem rastreável. Google chama a mesma ideia de **UDF — Unidirectional Data Flow**: estado flui para baixo, eventos fluem para cima, sempre em uma única direção.

Na prática, dentro de uma `ViewModel`, isso vira três tipos:

| Peça | Pergunta que responde | Tipo Kotlin usado |
| --- | --- | --- |
| **State** | "O que a tela deve mostrar agora?" | `data class` + `StateFlow` |
| **Event** | "O que o usuário (ou o sistema) acabou de fazer?" | `sealed interface` |
| **Effect** | "O que precisa acontecer uma única vez (não é 'estado')?" | `sealed interface` + `SharedFlow` |

A UI (uma tela Compose ou uma Activity com Views) só faz duas coisas: **envia `Event`s** para a ViewModel através de um único método `onEvent(event)`, e **observa** o `State` (sempre) e o `Effect` (uma vez cada). A UI nunca decide nada sozinha — ela só reage ao que a ViewModel expõe.

O exemplo mais simples de ler primeiro neste projeto é o do Bluetooth, porque State, Event e Effect estão juntos no mesmo arquivo:
[`BluetoothState.kt`](../feature/bluetooth/presentation/src/main/kotlin/io/lb/bleandlistingopt/feature/bluetooth/presentation/BluetoothState.kt).

---

[↑ Índice](./index.md) · [Próximo: SRP →](./02-srp.md)
