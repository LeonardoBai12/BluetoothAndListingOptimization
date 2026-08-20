# SRP — Princípio da Responsabilidade Única

*Por que duas classes iguais podem ser duas responsabilidades diferentes*

SRP (Single Responsibility Principle) é o primeiro princípio do SOLID. A formulação final de Robert C. Martin, em *Clean Architecture* (Cap. 7), depois de descartar a versão mais popular e imprecisa ("uma função deve fazer uma coisa só"), é:

> *"Um módulo deve ser responsável perante um, e apenas um, ator."*
> (Martin, R.C. — *Clean Architecture*, Cap. 7 — The Single Responsibility Principle)

Um **ator**, aqui, é um grupo de pessoas (ou, no nosso caso, um "motivo de mudança") que compartilha uma razão para pedir alteração naquele código. O detalhe que costuma passar despercebido: o princípio fala de **motivo para mudar**, não de **forma da classe**. Duas classes podem ter exatamente os mesmos campos, os mesmos tipos, o mesmo formato — e ainda assim terem motivos completamente diferentes para mudar. Quando isso acontece, elas são responsabilidades diferentes, mesmo parecendo a mesma coisa.

**A armadilha comum:** ver duas classes idênticas e pensar "isso é duplicação, vou unificar em uma só". Às vezes é duplicação de verdade (mesma responsabilidade, código repetido por preguiça). Outras vezes são duas responsabilidades que, por coincidência, ainda têm a mesma forma hoje — e unificá-las cria um acoplamento que só vai doer quando uma das duas precisar mudar sem a outra.

## Exemplo real deste projeto

As telas de listagem (`feature:listing`) têm duas versões: uma com os anti-padrões de performance (`unoptimized`) e uma com as correções (`optimized`). No início deste projeto, o `State`, o `Event` e a `ViewModel` eram **uma única classe compartilhada** pelas duas telas, porque os campos eram idênticos.

*Antes — um State compartilhado por duas telas com motivos de mudança diferentes*

```kotlin
// um único arquivo, usado pelas duas telas
data class ListingUiState(
    val items: ImmutableList<ListingItem> = persistentListOf(),
    val query: String = "",
)

// a tela "ruim" precisava widenar o tipo na mão pra fingir que era
// instável -- o anti-padrão ficava escondido na assinatura da função,
// não no dado em si
```

*Depois — um State por responsabilidade, cada um no seu pacote*

[`unoptimized/UnoptimizedListState.kt`](../feature/listing/compose/src/main/kotlin/io/lb/bleandlistingopt/feature/listing/compose/unoptimized/UnoptimizedListState.kt):

```kotlin
data class UnoptimizedListState(
    val items: List<ListingItem> = emptyList(),
    val query: String = "",
)
```

[`optimized/OptimizedListState.kt`](../feature/listing/compose/src/main/kotlin/io/lb/bleandlistingopt/feature/listing/compose/optimized/OptimizedListState.kt):

```kotlin
data class OptimizedListState(
    val items: ImmutableList<ListingItem> = persistentListOf(),
    val query: String = "",
)
```

Repare que, aqui, os dois já nem são mais 100% idênticos: `items` é `List` num e `ImmutableList` no outro — exatamente porque essa diferença de tipo **é o anti-padrão e a correção sendo ensinados** (veja [State](./03-state.md) e o código de [`UnoptimizedListScreen.kt`](../feature/listing/compose/src/main/kotlin/io/lb/bleandlistingopt/feature/listing/compose/unoptimized/UnoptimizedListScreen.kt) / [`OptimizedListScreen.kt`](../feature/listing/compose/src/main/kotlin/io/lb/bleandlistingopt/feature/listing/compose/optimized/OptimizedListScreen.kt)). Separar os states deixou essa diferença de intenção explícita, em vez de escondida atrás de um tipo genérico compartilhado.

O mesmo refactor se repetiu, igual, no módulo RecyclerView (`feature:listing:xml`):
[`unoptimized/UnoptimizedRecyclerState.kt`](../feature/listing/xml/src/main/kotlin/io/lb/bleandlistingopt/feature/listing/xml/unoptimized/UnoptimizedRecyclerState.kt) e
[`optimized/OptimizedRecyclerState.kt`](../feature/listing/xml/src/main/kotlin/io/lb/bleandlistingopt/feature/listing/xml/optimized/OptimizedRecyclerState.kt).

> **Isso não é sobre evitar duplicação a qualquer custo.** Duplicar código tem um custo real (mais lugares para manter). A questão do SRP não é "duplicação é sempre boa" — é perguntar, antes de compartilhar: *essas duas coisas vão mudar pelo mesmo motivo, para sempre?* Se a resposta for "não, uma é o exemplo ruim e a outra é o exemplo bom, elas evoluem de forma independente por definição", compartilhar é a escolha errada mesmo que o código fique repetido hoje.

---

[↑ Índice](./index.md) · [Anterior: MVI e UDF](./01-mvi-e-udf.md) · [Próximo: State →](./03-state.md)
