---
layout: default
title: "Compose (LazyColumn)"
nav_order: 3
---

# Compose — LazyColumn

*Anti-padrões de recomposição vs. correções, código real lado a lado*

Duas telas renderizam os mesmos 3.000 itens fake em Compose: [`UnoptimizedListScreen`](https://github.com/LeonardoBai12/BluetoothAndListingOptimization/blob/main/feature/listing/compose/src/main/kotlin/io/lb/bleandlistingopt/feature/listing/compose/unoptimized/UnoptimizedListScreen.kt) e [`OptimizedListScreen`](https://github.com/LeonardoBai12/BluetoothAndListingOptimization/blob/main/feature/listing/compose/src/main/kotlin/io/lb/bleandlistingopt/feature/listing/compose/optimized/OptimizedListScreen.kt).

**Como medir a diferença:** no Layout Inspector do Android Studio, ative "Show recomposition counts" com a tela aberta, depois digite no campo de busca. No `Unoptimized`, a contagem de toda linha visível sobe a cada tecla; no `Optimized`, só as linhas que realmente mudaram.

## Antes de tudo: o que é "recomposição" e "estabilidade"

Compose reexecuta (recompõe) uma função `@Composable` quando o `State` que ela lê muda. Reexecutar a árvore inteira a cada mudança pequena seria caro demais, então o compilador do Compose tenta **pular** a recomposição de uma função se conseguir provar que os parâmetros dela são iguais aos da última vez — essa prova só é possível se o parâmetro for de um tipo **estável**.

Um tipo é estável, pela definição do compilador, se: (1) `equals()` sempre retorna o mesmo resultado para duas instâncias com as mesmas propriedades públicas; (2) toda vez que uma propriedade pública muda, a composição é notificada disso; (3) todo tipo das propriedades públicas também é estável. Tipos primitivos, `String` e classes `data class` com só `val` de tipos estáveis passam nessa prova automaticamente. Interfaces genéricas como `List` e classes com `var` **não passam** — e é exatamente aí que moram os dois primeiros anti-padrões abaixo.

Quando um parâmetro é instável, o Compose não consegue confiar na comparação e simplesmente **desiste de pular** a recomposição daquela função — ela reexecuta sempre que a função pai reexecutar, tenha o dado mudado de verdade ou não.

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

`List` é uma interface — o valor real por trás dela poderia ser uma `MutableList` alterada em algum outro lugar do código, fora da visão do Compose. Como o compilador não tem como garantir a regra 2 (mudança notifica a composição) para esse tipo, ele marca `items` como instável, mesmo que, na prática, o `ViewModel` deste projeto nunca mute a lista depois de criá-la. O compilador não sabe disso — ele só enxerga o tipo declarado na assinatura da função, não o comportamento real do código que a chama.

*Solução* — [`OptimizedListScreen.kt`](https://github.com/LeonardoBai12/BluetoothAndListingOptimization/blob/main/feature/listing/compose/src/main/kotlin/io/lb/bleandlistingopt/feature/listing/compose/optimized/OptimizedListScreen.kt):

```kotlin
@Composable
private fun OptimizedListContent(
    items: ImmutableList<ListingItem>,
    query: String,
    onEvent: (OptimizedListEvent) -> Unit,
)
```

`ImmutableList` (de `kotlinx.collections.immutable`) não expõe nenhum método de mutação na sua API — não existe `add`/`remove`/`set` para chamar. Isso é uma garantia em nível de tipo, não de convenção, e é isso que deixa o compilador do Compose confiar que a regra 2 vale sempre para esse tipo, sem precisar de anotação nenhuma. Com esse parâmetro estável, `OptimizedListContent` pode ser pulada quando `items` é a mesma lista (por igualdade estrutural) da última recomposição.

## Modelo mutável vs. modelo estável

*Problema* — `UnoptimizedListScreen.kt`:

```kotlin
private class UnstableRow(val item: ListingItem, var isFavorite: Boolean)
```

`var isFavorite` é uma propriedade mutável comum — mudar o valor dela não passa por nenhum mecanismo que avise o Compose (isso é o que `mutableStateOf` faz; um `var` de classe normal não faz). Como o compilador não consegue provar a regra 2 para essa classe, `UnstableRow` inteira vira instável — e junto com ela, todo composable que a recebe como parâmetro. O `remember(item.id) { UnstableRow(item, item.isFavorite) }` que embrulha a criação não resolve nada disso: ele só garante que a mesma instância seja reaproveitada enquanto `item.id` não mudar, mas o composable `UnoptimizedRow(row: UnstableRow, ...)` continua marcado como sempre-recompõe, porque a instabilidade vem da *forma da classe*, não de quando ela é criada.

*Solução* — `OptimizedListScreen.kt`: nenhum wrapper é necessário. `ListingItem` (o modelo compartilhado do projeto, em `core:common`) já é estável para o Compose, porque toda propriedade dela é um `val` de um tipo primitivo/`String` — passa nas três regras sem precisar de anotação. A correção aqui não é "adicionar algo", é remover o wrapper que quebrava a estabilidade e usar o modelo de domínio direto.

## `key` e `contentType`

*Problema* — `UnoptimizedListScreen.kt`:

```kotlin
LazyColumn {
    items(items) { item -> ... }
}
```

Sem `key`, a `LazyColumn` usa a **posição** de cada item na lista como identidade implícita. Isso tem duas consequências, uma de correção e uma de performance. A de correção: se uma linha guarda estado próprio (por exemplo, um `remember { mutableStateOf(false) }` interno para "expandido/recolhido"), esse estado fica preso à *posição*, não ao *item lógico* — depois de um insert/remove/reorder, o item que passa a ocupar aquela posição herda o estado que era de outro item, um bug visível, não só lentidão. A de performance: como o Compose não tem como saber que o item na posição 5 é "o mesmo" de antes só que deslocado, ele reconstrói cada linha depois do ponto de mudança em vez de simplesmente mover o que já existia.

*Solução* — `OptimizedListScreen.kt`:

```kotlin
LazyColumn(state = listState) {
    items(items = items, key = { it.id }, contentType = { "row" }) { item -> ... }
}
```

`key = { it.id }` dá ao Compose uma identidade real e estável por item, independente de posição — inserir/remover/reordenar move o estado junto com o item certo, em vez de reconstruir tudo depois do ponto de mudança. `contentType` resolve um problema diferente: quando uma `LazyColumn` mistura linhas de formatos diferentes (cabeçalhos, anúncios, itens normais), o pool de reciclagem de composições do Compose só reaproveita uma composição para outra do **mesmo** `contentType` — sem isso, ele tentaria reaproveitar composições de formatos incompatíveis, o que forçaria descartar e recompor do zero de qualquer jeito. Neste projeto todas as linhas têm o mesmo formato (`contentType = "row"` fixo), então o ganho aqui é sobretudo de correção/boa prática para quando a lista crescer, não algo visivelmente mensurável hoje.

## Leitura de estado alto demais na árvore

*Problema* — `UnoptimizedListScreen.kt`:

```kotlin
@Composable
private fun UnoptimizedListContent(
    items: List<ListingItem>,
    query: String,
    onEvent: (UnoptimizedListEvent) -> Unit,
) {
    Column {
        TextField(value = query, onValueChange = { onEvent(UnoptimizedListEvent.OnQueryChange(it)) })
        LazyColumn {
            items(items) { item -> ... }
        }
    }
}
```

`query` e `items` são lidos como parâmetros da **mesma** função composable, e o campo de busca e a `LazyColumn` são construídos dentro do **mesmo** corpo de função. O escopo de recomposição do Compose é por chamada de função composable — a menor unidade que ele consegue reexecutar de forma independente é o corpo inteiro de uma função `@Composable`. Como `query` muda a cada tecla digitada, e `query` é lido aqui, o corpo inteiro de `UnoptimizedListContent` é candidato a reexecutar a cada tecla — `TextField` **e** a configuração da `LazyColumn` juntos — mesmo que `items` não tenha mudado nada.

*Solução* — `OptimizedListScreen.kt`:

```kotlin
@Composable
private fun OptimizedListContent(
    items: ImmutableList<ListingItem>,
    query: String,
    onEvent: (OptimizedListEvent) -> Unit,
) {
    val listState = rememberLazyListState()
    Column {
        SearchField(query = query, onQueryChange = { onEvent(OptimizedListEvent.OnQueryChange(it)) }, listState = listState)
        ListingLazyColumn(items = items, listState = listState, onToggleFavorite = { ... })
    }
}
```

Separar `SearchField` e `ListingLazyColumn` em duas funções `@Composable` distintas cria dois escopos de recomposição independentes. Quando `query` muda, só a chamada `SearchField(query = ...)` precisa reexecutar — `ListingLazyColumn(items = ..., ...)` recebe os mesmos argumentos de antes, então o Compose nem entra nela.

## `remember` para trabalho caro

*Problema* — `UnoptimizedListScreen.kt`:

```kotlin
@Composable
private fun UnoptimizedRow(row: UnstableRow, onToggle: () -> Unit) {
    val formattedPrice = formatPriceSlowly(row.item.price) // roda toda vez
    ...
}
```

Essa linha executa toda vez que o corpo de `UnoptimizedRow` roda — e, como `UnstableRow` é instável (seção acima), o corpo roda a cada recomposição do pai, ou seja, a cada tecla digitada no campo de busca, para toda linha visível. `formatPriceSlowly` aqui é uma formatação simulada como lenta (`Thread.sleep(1)`) — com ~10 linhas visíveis, isso sozinho já soma uns 10ms de trabalho extra na main thread por tecla digitada, sem contar o resto.

*Solução* — `OptimizedListScreen.kt`:

```kotlin
val formattedPrice = remember(item.price) { formatPriceSlowly(item.price) }
```

`remember` guarda o resultado do lambda associado a uma chave (`item.price`, aqui). Numa recomposição, se a chave for igual à da vez anterior, o valor guardado é devolvido direto, sem reexecutar o lambda — `formatPriceSlowly` só roda de novo quando o preço realmente mudar.

## `derivedStateOf` para valores derivados de leitura frequente

```kotlin
val showScrollHint by remember { derivedStateOf { listState.firstVisibleItemIndex > 5 } }
```

`listState.firstVisibleItemIndex` é, por baixo, um `State<Int>` que muda a cada posição de rolagem — ler ele direto no corpo de um composable inscreveria esse composable para recompor a cada pixel/índice rolado. O que a tela realmente precisa saber é um booleano bem mais grosseiro (passou do item 5 ou não). `derivedStateOf` embrulha a leitura de alta frequência: o `State<Boolean>` que ele produz só notifica quem o lê quando o **valor calculado** muda — a lambda interna é reavaliada a cada scroll, mas a recomposição de quem consome `showScrollHint` só é disparada nos poucos momentos em que o resultado realmente vira `true`/`false`.

## Leitura de estado adiada via lambda de Modifier

Se o brilho do campo de busca ao rolar a lista fosse escrito do jeito ingênuo, ficaria assim:

*Jeito ingênuo (não é o código real do projeto, serve só para mostrar o problema):*

```kotlin
val isScrolled = listState.firstVisibleItemIndex > 0 // lido no corpo do composable
TextField(
    modifier = Modifier.graphicsLayer { shadowElevation = if (isScrolled) 8f else 0f },
)
```

Ler `firstVisibleItemIndex` diretamente no corpo do composable inscreve `SearchField` inteiro para recompor a cada tick de rolagem — mesmo que a única coisa que realmente depende da posição de scroll seja um único `Float` passado para `graphicsLayer`.

*Solução real* — `OptimizedListScreen.kt`:

```kotlin
TextField(
    modifier = Modifier.graphicsLayer {
        shadowElevation = if (listState.firstVisibleItemIndex > 0) 8f else 0f
    },
)
```

O lambda passado para `graphicsLayer` não roda durante a composição — ele roda na **fase de desenho**, a cada frame que a layer é desenhada, por fora do mecanismo de recomposição do Compose. Ler o `State` do scroll ali dentro faz o valor ser lido a cada frame (a sombra continua atualizando suave, em tempo real), mas sem nunca inscrever o corpo de `SearchField` como dependente dele — só a fase de desenho é reexecutada, não a composição. O mesmo truque (ler dentro do lambda do `Modifier`, não no corpo do composable) vale para `Modifier.offset { }`, `Modifier.drawWithContent { }` e afins.

## Lambda hoisted e estável

```kotlin
val onClick = remember(item.id) { { onToggleFavorite(item.id) } }
```

Sem `remember`, escrever `{ onToggleFavorite(item.id) }` direto no `onClick`/`onCheckedChange` cria uma instância de lambda nova a cada recomposição da linha — mesmo tendo o mesmo comportamento, é um objeto diferente a cada vez, o que pode impedir otimizações internas de composables como `Checkbox` que comparam a lambda recebida entre uma recomposição e outra. Vale um adendo honesto: o compilador do Compose já faz memoização automática de lambdas simples escritas direto num argumento de chamada de composable, quando tudo que ela captura é estável — então, para uma chamada isolada, esse `remember` explícito pode já ser redundante com o que o compilador faz sozinho. Ele deixa de ser redundante aqui porque `onClick` é **uma única instância reaproveitada em dois lugares** (no `clickable` da `Row` e, indiretamente, no `onCheckedChange` do `Checkbox`) — nesse caso, `remember` garante a mesma instância nos dois usos de um jeito que não depende de detalhes de versão do compilador.

---

[↑ Índice](./) · [Anterior: RecyclerView](./01-recyclerview/) · [Próximo: Bluetooth (GATT) →](./03-ble-gatt/)
