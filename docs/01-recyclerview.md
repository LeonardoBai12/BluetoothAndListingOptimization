---
layout: default
title: "RecyclerView"
nav_order: 2
---

# RecyclerView

*Anti-padrões de performance vs. correções, código real lado a lado*

Duas telas renderizam a mesma lista de 3.000 itens fake: [`UnoptimizedRecyclerActivity`](https://github.com/LeonardoBai12/BluetoothAndListingOptimization/tree/main/feature/listing/xml/src/main/kotlin/io/lb/bleandlistingopt/feature/listing/xml/unoptimized) e [`OptimizedRecyclerActivity`](https://github.com/LeonardoBai12/BluetoothAndListingOptimization/tree/main/feature/listing/xml/src/main/kotlin/io/lb/bleandlistingopt/feature/listing/xml/optimized). Cada anti-padrão abaixo tem seu par corrigido, no mesmo lugar do adapter.

**Como medir a diferença:** `adb shell dumpsys gfxinfo <pkg> reset` antes de rolar, role as duas telas a mesma quantidade, depois `adb shell dumpsys gfxinfo <pkg>` e compare "Number Missed Vsync" / janky frames. Para detalhe quadro a quadro, capture um trace do Perfetto enquanto rola.

## `notifyDataSetChanged()` vs. `DiffUtil`

*Problema* — [`UnoptimizedRecyclerAdapter.kt`](https://github.com/LeonardoBai12/BluetoothAndListingOptimization/blob/main/feature/listing/xml/src/main/kotlin/io/lb/bleandlistingopt/feature/listing/xml/unoptimized/UnoptimizedRecyclerAdapter.kt):

```kotlin
fun updateItems(newItems: List<ListingItem>) {
    items = newItems
    notifyDataSetChanged()
}
```

Toda chamada rebinda **todos** os itens visíveis do zero, mesmo que só o favorito de um item tenha mudado — não existe como o RecyclerView saber o que realmente diferiu.

*Solução* — [`OptimizedRecyclerAdapter.kt`](https://github.com/LeonardoBai12/BluetoothAndListingOptimization/blob/main/feature/listing/xml/src/main/kotlin/io/lb/bleandlistingopt/feature/listing/xml/optimized/OptimizedRecyclerAdapter.kt):

```kotlin
private object ListingDiffCallback : DiffUtil.ItemCallback<ListingItem>() {
    override fun areItemsTheSame(oldItem: ListingItem, newItem: ListingItem) = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: ListingItem, newItem: ListingItem) = oldItem == newItem

    override fun getChangePayload(oldItem: ListingItem, newItem: ListingItem): Any? =
        if (oldItem.isFavorite != newItem.isFavorite && oldItem.copy(isFavorite = newItem.isFavorite) == newItem) {
            PAYLOAD_FAVORITE
        } else {
            null
        }
}

class OptimizedRecyclerAdapter(...) : ListAdapter<ListingItem, ViewHolder>(ListingDiffCallback)
```

`ListAdapter` roda o diff numa thread de background e despacha só os `insert`/`remove`/`change` granulares que o diff encontrou — sem `notifyDataSetChanged()` em lugar nenhum desse caminho.

## Payload parcial vs. rebind completo

Repare no `getChangePayload` acima: quando só `isFavorite` muda, ele retorna `PAYLOAD_FAVORITE` em vez de `null`. O `AsyncListDiffer` do `ListAdapter` chama `notifyItemChanged(position, payload)` automaticamente para esse caso, o que faz `onBindViewHolder` receber o payload:

```kotlin
override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
    if (payloads.contains(PAYLOAD_FAVORITE)) {
        // só o checkbox é tocado -- title/price/tags ficam exatamente
        // como estavam, nenhuma reinflação de nada
        bindFavorite(holder, getItem(position))
        return
    }
    onBindViewHolder(holder, position)
}
```

## `holder.setIsRecyclable(false)`

*Problema* — [`UnoptimizedRecyclerAdapter.kt`](https://github.com/LeonardoBai12/BluetoothAndListingOptimization/blob/main/feature/listing/xml/src/main/kotlin/io/lb/bleandlistingopt/feature/listing/xml/unoptimized/UnoptimizedRecyclerAdapter.kt):

```kotlin
holder.setIsRecyclable(false)
```

Isso diz ao RecyclerView que esse holder nunca deve voltar para o pool de reciclagem. Toda linha que rola para fora da tela é descartada em vez de reaproveitada, o que inunda o sistema de novas inflações para linhas que voltam a aparecer — exatamente o oposto do que o RecyclerView (recycler + view) existe para evitar. A correção é simplesmente **não chamar isso**.

## `RecycledViewPool` não compartilhado (RecyclerView aninhado)

Cada linha tem uma segunda RecyclerView horizontal por dentro, com as tags do item.

*Problema* — [`UnoptimizedRecyclerAdapter.kt`](https://github.com/LeonardoBai12/BluetoothAndListingOptimization/blob/main/feature/listing/xml/src/main/kotlin/io/lb/bleandlistingopt/feature/listing/xml/unoptimized/UnoptimizedRecyclerAdapter.kt):

```kotlin
binding.tagsRecyclerView.adapter = TagsAdapter(tagsFor(item))
```

Um adapter novo (e, implicitamente, um `RecycledViewPool` novo) a cada bind, sem pool nenhum compartilhado entre linhas. A linha N+1 nunca pode reaproveitar as views de chip que a linha N já inflou.

*Solução* — [`OptimizedRecyclerActivity.kt`](https://github.com/LeonardoBai12/BluetoothAndListingOptimization/blob/main/feature/listing/xml/src/main/kotlin/io/lb/bleandlistingopt/feature/listing/xml/optimized/OptimizedRecyclerActivity.kt):

```kotlin
// um pool só, compartilhado pela RecyclerView de tags de cada linha
private val sharedTagsPool = RecyclerView.RecycledViewPool()
private val adapter = OptimizedRecyclerAdapter(
    sharedTagsPool = sharedTagsPool,
    onFavoriteToggle = { id -> viewModel.onEvent(OptimizedRecyclerEvent.OnFavoriteToggle(id)) },
)
```

E em [`OptimizedRecyclerAdapter.kt`](https://github.com/LeonardoBai12/BluetoothAndListingOptimization/blob/main/feature/listing/xml/src/main/kotlin/io/lb/bleandlistingopt/feature/listing/xml/optimized/OptimizedRecyclerAdapter.kt), passado para cada `ViewHolder` no `onCreateViewHolder`:

```kotlin
binding.tagsRecyclerView.setRecycledViewPool(sharedTagsPool)
```

## IDs estáveis e `setHasFixedSize`

*Solução* — `OptimizedRecyclerAdapter.kt`:

```kotlin
init {
    setHasStableIds(true)
}

override fun getItemId(position: Int): Long = getItem(position).id
```

Combinado com `setHasStableIds(true)` na Activity, isso deixa o RecyclerView rastrear a identidade de uma linha através de uma mudança no dataset mesmo quando sua posição se move — em vez de assumir "posição N ainda é a mesma view" (o padrão, e errado assim que as linhas reordenam).

Em [`OptimizedRecyclerActivity.kt`](https://github.com/LeonardoBai12/BluetoothAndListingOptimization/blob/main/feature/listing/xml/src/main/kotlin/io/lb/bleandlistingopt/feature/listing/xml/optimized/OptimizedRecyclerActivity.kt):

```kotlin
binding.listingRecyclerView.apply {
    layoutManager = LinearLayoutManager(this@OptimizedRecyclerActivity)
    adapter = this@OptimizedRecyclerActivity.adapter
    setHasFixedSize(true)   // altura da linha não depende do conteúdo
    setItemViewCacheSize(8) // mantém algumas linhas fora da tela já bindadas
}
```

## Layout profundamente aninhado

*Problema* — [`item_listing_unoptimized.xml`](https://github.com/LeonardoBai12/BluetoothAndListingOptimization/blob/main/feature/listing/xml/src/main/res/layout/item_listing_unoptimized.xml): `LinearLayout` dentro de `LinearLayout` dentro de `FrameLayout` dentro de `LinearLayout` — cada nível extra é mais uma passada de measure/layout que o sistema precisa percorrer para cada linha visível.

*Solução* — [`item_listing_optimized.xml`](https://github.com/LeonardoBai12/BluetoothAndListingOptimization/blob/main/feature/listing/xml/src/main/res/layout/item_listing_optimized.xml): um único `ConstraintLayout` plano, mesma UI, uma passada de measure/layout para a linha inteira.

## Trabalho pesado em `onBindViewHolder`

O mesmo `formatPriceSlowly()` (uma formatação simulada como lenta) roda nos dois adapters — a diferença não é o trabalho em si, é a frequência: no `Unoptimized`, roda em todo bind causado por `notifyDataSetChanged()`; no `Optimized`, o `DiffUtil` já garante que `onBindViewHolder` só roda para linhas novas ou genuinamente alteradas.

---

[↑ Índice](./) · [Próximo: Compose (LazyColumn) →](./02-compose-lazycolumn/)
