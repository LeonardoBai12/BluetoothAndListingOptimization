# Armadilhas comuns

### 1. Effect dentro do State

Já coberto em [Effect](./05-effect.md) — causa reexecução do efeito em qualquer reemissão do State (rotação, retorno de outra tela, etc). Snackbar/Toast/navegação sempre vão em `Effect`, nunca em campo de `State`.

### 2. State incompleto (múltiplas fontes de verdade)

Se a Activity/Composable guarda uma `var` ou `remember { mutableStateOf(...) }` própria para controlar algo que é visualmente relevante, isso é um `State` que vazou pra fora da ViewModel. Sintoma: comportamento que "esquece" em rotação de tela porque só existia como `remember`, que não sobrevive a mudança de configuração sem `rememberSaveable`.

### 3. Compartilhar contrato (State/Event/Effect) por parecer igual

O assunto inteiro de [SRP](./02-srp.md). Duas telas com o mesmo formato de dados hoje não significa que vão evoluir juntas amanhã.

### 4. `when` não exaustivo

Se `Event` for uma `interface` comum (não `sealed`), o compilador não consegue garantir que o `when` trata todos os casos, e um `else -> Unit` silencioso pode esconder um evento esquecido. Sempre `sealed interface`/`sealed class` — ver [Event](./04-event.md).

### 5. ViewModel decidindo UI, e não estado

O `State` deve descrever *dados* ("connectionState é Connected(mtu=247)"), não decisões visuais ("mostrar o botão azul"). Quem traduz dado em decisão visual é a função `@Composable`, olhando pro State — isso mantém a ViewModel testável sem precisar de nada de Compose/Android nos testes. Veja
[`UnoptimizedListViewModelTest.kt`](../feature/listing/compose/src/test/kotlin/io/lb/bleandlistingopt/feature/listing/compose/unoptimized/UnoptimizedListViewModelTest.kt)
para um exemplo de teste de redução de estado que não toca em nada de Android/Compose.

---

[↑ Índice](./index.md) · [Anterior: ANR Lab](./07-anr-lab-pares.md) · [Próximo: Checklist e mapa do código →](./09-checklist-e-mapa.md)
