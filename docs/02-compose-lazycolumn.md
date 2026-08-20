---
layout: default
title: "Compose (LazyColumn)"
nav_order: 3
---

# Compose — LazyColumn

*Anti-padrões de recomposição vs. correções, código real lado a lado*

Duas telas renderizam os mesmos 3.000 itens fake em Compose: [`UnoptimizedListScreen`](https://github.com/LeonardoBai12/BluetoothAndListingOptimization/blob/main/feature/listing/compose/src/main/kotlin/io/lb/bleandlistingopt/feature/listing/compose/unoptimized/UnoptimizedListScreen.kt) e [`OptimizedListScreen`](https://github.com/LeonardoBai12/BluetoothAndListingOptimization/blob/main/feature/listing/compose/src/main/kotlin/io/lb/bleandlistingopt/feature/listing/compose/optimized/OptimizedListScreen.kt).

**Como medir a diferença:** no Layout Inspector do Android Studio, ative "Show recomposition counts" com a tela aberta, depois digite no campo de busca. No `Unoptimized`, a contagem de toda linha visível sobe a cada tecla; no `Optimized`, só as linhas que realmente mudaram.

## `List` vs. `ImmutableList`

*Problema* — [`UnoptimizedListScreen.kt`](https://github.com/LeonardoBai12/BluetoothAndListingOptimization/blob/main/feature/listing/compose/src/main/kotlin/io/lb/bleandlistingopt/feature/listing/compose/unoptimized/UnoptimizedListScreen.kt):

```kotlin
@Composable
private fun UnoptimizedListContent(
    items: List<ListingItem>,
    query: String,
    onEvent: (UnoptimizedListEvent) -> Unit,
)
```

`List` não é um tipo que o compilador do Compose consegue provar estável — poderia ser uma `MutableList` mutada depois de passada adiante. Esse parâmetro, e tudo que lê ele adiante, é tratado como instável, o que derruba o skip de recomposição independente do dado ter mudado de verdade ou não.

*Solução* — [`OptimizedListScreen.kt`](https://github.com/LeonardoBai12/BluetoothAndListingOptimization/blob/main/feature/listing/compose/src/main/kotlin/io/lb/bleandlistingopt/feature/listing/compose/optimized/OptimizedListScreen.kt):

```kotlin
@Composable
private fun OptimizedListContent(
    items: ImmutableList<ListingItem>,
    query: String,
    onEvent: (OptimizedListEvent) -> Unit,
)
```

`ImmutableList` (de `kotlinx.collections.immutable`) o compilador reconhece como estável sozinho — sem wrapper, sem anotação, sem risco de alguém passar uma lista mutável por baixo de um tipo alargado.

## Modelo mutável vs. modelo estável

*Problema* — `UnoptimizedListScreen.kt`:

```kotlin
// `var` torna essa classe instável ao compilador do Compose -- ele não
// consegue provar que o valor não muda entre recomposições, então todo
// composable que lê essa classe é forçado a recompor em vez de ser
// pulado, mesmo quando nada realmente mudou.
private class UnstableRow(val item: ListingItem, var isFavorite: Boolean)
```

*Solução* — `OptimizedListScreen.kt`: nenhum wrapper é necessário. `ListingItem` já é estável para o Compose — toda propriedade é um `val` de um tipo estável — diferente de `UnstableRow`, que precisava de um `var`.

## `key` e `contentType`

*Problema* — `UnoptimizedListScreen.kt`:

```kotlin
LazyColumn {
    items(items) { item -> ... }
}
```

Sem `key`, o Compose identifica cada linha pela posição na lista: inserir, remover ou reordenar faz ele pensar que toda linha depois do ponto de mudança é "um item diferente no mesmo slot" e reconstrói do zero em vez de só mover o estado.

*Solução* — `OptimizedListScreen.kt`:

```kotlin
LazyColumn(state = listState) {
    items(items = items, key = { it.id }, contentType = { "row" }) { item -> ... }
}
```

`key` deixa o Compose rastrear cada linha pela identidade em vez da posição; `contentType` deixa linhas do mesmo tipo reaproveitarem composição/layout entre si durante o scroll, mesmo se a lista algum dia misturar formatos de linha.

## Leitura de estado alto demais na árvore

*Problema* — `UnoptimizedListScreen.kt`: o campo de busca e a `LazyColumn` estão dentro do mesmo escopo de composable, então `query` é lido no mesmo lugar que renderiza a lista inteira — o Compose não consegue pular "só o TextField", a função inteira reexecuta a cada tecla, lista incluída.

*Solução* — `OptimizedListScreen.kt`: `SearchField` e `ListingLazyColumn` são composables separados; `query` nunca chega ao escopo da `LazyColumn`, então digitar só recompõe `SearchField`.

## `remember` / `derivedStateOf`

*Problema* — `UnoptimizedListScreen.kt`:

```kotlin
@Composable
private fun UnoptimizedRow(row: UnstableRow, onToggle: () -> Unit) {
    // roda em TODA recomposição desta linha, não só quando o preço muda
    val formattedPrice = formatPriceSlowly(row.item.price)
    ...
}
```

*Solução* — `OptimizedListScreen.kt`:

```kotlin
val formattedPrice = remember(item.price) { formatPriceSlowly(item.price) }
```

E, para um valor derivado de algo que muda a cada pixel do scroll, `derivedStateOf`:

```kotlin
// firstVisibleItemIndex muda a cada pixel rolado, mas este Boolean só
// muda -- e só então dispara recomposição de quem o lê -- quando a
// posição de scroll cruza o limite
val showScrollHint by remember { derivedStateOf { listState.firstVisibleItemIndex > 5 } }
```

## Leitura de estado adiada via lambda de Modifier

*Solução* — `OptimizedListScreen.kt`:

```kotlin
TextField(
    modifier = Modifier.graphicsLayer {
        // roda na fase de desenho, não na composição -- ler `listState`
        // aqui em vez de via `by remember` no corpo do composable faz o
        // scroll atualizar a sombra sem nunca recompor o campo de busca
        shadowElevation = if (listState.firstVisibleItemIndex > 0) 8f else 0f
    },
)
```

## Lambda hoisted e estável

*Solução* — `OptimizedListScreen.kt`:

```kotlin
// lembrado por item.id em vez de uma closure nova a cada recomposição
val onClick = remember(item.id) { { onToggleFavorite(item.id) } }
```

---

[↑ Índice](./) · [Anterior: RecyclerView](./01-recyclerview/) · [Próximo: Bluetooth (GATT) →](./03-ble-gatt/)
