# Checklist e mapa do código

## Checklist rápido

- [ ] `State` é uma `data class` imutável, com valores padrão, e é a **única** fonte de verdade da tela.
- [ ] `Event` é um `sealed interface`, um `when` exaustivo trata cada variante, e a UI só chama `onEvent(...)` — nunca métodos soltos da ViewModel.
- [ ] `Effect` é para o que acontece uma vez (mensagem, navegação) e vive num `SharedFlow`, nunca dentro do `State`.
- [ ] Antes de compartilhar um `State`/`Event`/`Effect` entre duas telas só porque "são iguais hoje", perguntar: elas mudam pelo mesmo motivo, sempre? Se não, duplicar.

## Onde ver isso no código

| Arquivo | O que mostra |
| --- | --- |
| [`BluetoothState.kt`](../feature/bluetooth/presentation/src/main/kotlin/io/lb/bleandlistingopt/feature/bluetooth/presentation/BluetoothState.kt) | State + Event + Effect juntos num arquivo só — o exemplo mais simples para ler primeiro |
| [`AnrState.kt`](../feature/anr/src/main/kotlin/io/lb/bleandlistingopt/feature/anr/AnrState.kt) | Event com 8 variantes emparelhadas (trigger/fix), todas `data object` |
| [`AnrViewModel.kt`](../feature/anr/src/main/kotlin/io/lb/bleandlistingopt/feature/anr/AnrViewModel.kt) | Os 4 pares completos de problema/solução — ver [ANR Lab](./07-anr-lab-pares.md) |
| [`feature/listing/compose/.../unoptimized/`](../feature/listing/compose/src/main/kotlin/io/lb/bleandlistingopt/feature/listing/compose/unoptimized) | Pacote autossuficiente: State + Event + Effect + ViewModel + Screen, tudo separado do `optimized/` |
| [`feature/listing/compose/.../optimized/`](../feature/listing/compose/src/main/kotlin/io/lb/bleandlistingopt/feature/listing/compose/optimized) | O par do de cima — compare os dois `State` lado a lado para ver o SRP na prática |
| [`BluetoothScreen.kt`](../feature/bluetooth/presentation/src/main/kotlin/io/lb/bleandlistingopt/feature/bluetooth/presentation/BluetoothScreen.kt) | `collectAsStateWithLifecycle()` em ação, mais tratamento de permissões usando State |

---

[↑ Índice](./index.md) · [Anterior: Armadilhas comuns](./08-armadilhas-comuns.md)
