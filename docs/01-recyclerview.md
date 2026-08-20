---
layout: default
title: "RecyclerView"
nav_order: 2
---

# RecyclerView

*Anti-padrões de performance vs. correções, código real lado a lado*

Duas telas renderizam a mesma lista de 3.000 itens fake: [`UnoptimizedRecyclerActivity`](https://github.com/LeonardoBai12/BluetoothAndListingOptimization/tree/main/feature/listing/xml/src/main/kotlin/io/lb/bleandlistingopt/feature/listing/xml/unoptimized) e [`OptimizedRecyclerActivity`](https://github.com/LeonardoBai12/BluetoothAndListingOptimization/tree/main/feature/listing/xml/src/main/kotlin/io/lb/bleandlistingopt/feature/listing/xml/optimized). Cada anti-padrão abaixo tem seu par corrigido, no mesmo lugar do adapter.

**Como medir a diferença:** `adb shell dumpsys gfxinfo <pkg> reset` antes de rolar, role as duas telas a mesma quantidade, depois `adb shell dumpsys gfxinfo <pkg>` e compare "Number Missed Vsync" / janky frames. Para detalhe quadro a quadro, capture um trace do Perfetto enquanto rola.

## Antes de tudo: por que "reciclar" existe

Uma `RecyclerView` não cria uma `View` para cada item da lista — ela cria só `View`s suficientes para preencher a tela visível mais uma margem pequena. Quando uma linha rola para fora, seu `ViewHolder` não é destruído: ele vai para um **pool de reciclagem**, e quando uma nova linha está prestes a entrar na tela, a `RecyclerView` pega um `ViewHolder` do pool (em vez de inflar um layout novo do zero, uma operação que envolve I/O de XML e construção de árvore de `View`s) e só chama `onBindViewHolder` para preencher com os dados novos. Inflar é caro; rebindar é barato. Todo anti-padrão abaixo é, de um jeito ou de outro, uma forma de jogar fora esse reaproveitamento — seja recusando participar dele (`setIsRecyclable(false)`), seja fazendo mais trabalho do que precisa a cada rebind, seja não dando ao sistema informação suficiente para saber o que realmente mudou.

## `notifyDataSetChanged()` vs. `DiffUtil`

*Problema* — [`UnoptimizedRecyclerAdapter.kt`](https://github.com/LeonardoBai12/BluetoothAndListingOptimization/blob/main/feature/listing/xml/src/main/kotlin/io/lb/bleandlistingopt/feature/listing/xml/unoptimized/UnoptimizedRecyclerAdapter.kt):

```kotlin
fun updateItems(newItems: List<ListingItem>) {
    items = newItems
    notifyDataSetChanged()
}
```

`notifyDataSetChanged()` não diz *o quê* mudou — só diz "o dataset inteiro pode ter mudado, de qualquer jeito". A `RecyclerView` reage tratando toda `ViewHolder` atualmente vinculada como potencialmente inválida: chama `onBindViewHolder` de novo para **todas** as posições visíveis, mesmo que só o favorito de um item tenha mudado, e desiste de qualquer animação de item (mover, inserir, remover) porque não tem como saber quais posições correspondem aos mesmos itens de antes.

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

`ListAdapter` guarda a lista antiga internamente; quando você chama `submitList(novaLista)`, ele roda o algoritmo de diff (baseado em [Eugene W. Myers, "An O(ND) Difference Algorithm"](https://xlinux.nist.gov/dads/HTML/myersDiff.html), a mesma família de algoritmo por trás de `diff`/`git diff`) numa thread de background, comparando `oldItem`/`newItem` par a par via `areItemsTheSame` (identidade) e `areContentsTheSame` (conteúdo). O resultado é uma sequência mínima de `insert`/`remove`/`move`/`change`, despachada de volta para a `RecyclerView` já granular — sem `notifyDataSetChanged()` em lugar nenhum desse caminho, e com animações de item funcionando de graça.

## Payload parcial vs. rebind completo

Repare no `getChangePayload` acima: quando só `isFavorite` muda (o resto do item continua igual), ele retorna `PAYLOAD_FAVORITE` em vez de `null`. Isso muda o que o `AsyncListDiffer` do `ListAdapter` despacha: em vez de `notifyItemChanged(position)` puro (que causaria um rebind completo daquela linha), ele chama `notifyItemChanged(position, payload)` — e é esse `payload` que chega em `onBindViewHolder`:

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

Sem esse overload, todo `notifyItemChanged` cai no `onBindViewHolder(holder, position)` de um argumento só — que reatribui *todo* o conteúdo da linha (título, preço, tags, favorito), mesmo quando um único booleano mudou.

## `holder.setIsRecyclable(false)`

*Problema* — [`UnoptimizedRecyclerAdapter.kt`](https://github.com/LeonardoBai12/BluetoothAndListingOptimization/blob/main/feature/listing/xml/src/main/kotlin/io/lb/bleandlistingopt/feature/listing/xml/unoptimized/UnoptimizedRecyclerAdapter.kt):

```kotlin
holder.setIsRecyclable(false)
```

Retomando a seção "por que reciclar existe": esse método diz explicitamente à `RecyclerView` que aquele `ViewHolder` específico nunca deve voltar para o pool. Quando a linha rola para fora da tela, em vez de ir para o pool para reaproveitamento futuro, ela é descartada — e a próxima linha que entrar na tela (mesmo que seja visualmente idêntica, um item que já existiu antes) força uma inflação nova do zero, com todo o custo de I/O de XML e montagem de `View` que reciclagem existe para evitar. A correção é simplesmente **não chamar isso**: `setIsRecyclable` existe para casos legítimos e raros (uma animação em andamento que não pode ser interrompida por um rebind), não como padrão.

## `RecycledViewPool` não compartilhado (RecyclerView aninhado)

Cada linha tem uma segunda `RecyclerView` horizontal por dentro, com as tags do item.

*Problema* — [`UnoptimizedRecyclerAdapter.kt`](https://github.com/LeonardoBai12/BluetoothAndListingOptimization/blob/main/feature/listing/xml/src/main/kotlin/io/lb/bleandlistingopt/feature/listing/xml/unoptimized/UnoptimizedRecyclerAdapter.kt):

```kotlin
binding.tagsRecyclerView.adapter = TagsAdapter(tagsFor(item))
```

Toda `RecyclerView` tem seu próprio `RecycledViewPool` por padrão, criado automaticamente na primeira vez que ela precisa de um. Como esse código atribui um `TagsAdapter` novo à `tagsRecyclerView` de cada linha a cada bind, cada uma das 3.000 `RecyclerView`s aninhadas (uma por linha da lista externa) acaba com o próprio pool isolado — a linha N+1 nunca pode reaproveitar as views de chip que a linha N já inflou, porque elas vivem em pools diferentes que nunca se comunicam.

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

Uma única instância de `RecycledViewPool`, criada uma vez na Activity e injetada em toda `ViewHolder` da lista externa. Agora existe só um pool de views de chip para as 3.000 `RecyclerView`s aninhadas compartilharem — uma chip-view descartada pela linha N está disponível para a linha N+1 reaproveitar.

## IDs estáveis e `setHasFixedSize`

*Solução* — `OptimizedRecyclerAdapter.kt`:

```kotlin
init {
    setHasStableIds(true)
}

override fun getItemId(position: Int): Long = getItem(position).id
```

Por padrão, a `RecyclerView` identifica um `ViewHolder` pela posição que ele ocupava. Com `setHasStableIds(true)` (chamado na `RecyclerView` em si, na Activity) e `getItemId` retornando o `id` real do item (não a posição), a `RecyclerView` passa a rastrear a identidade de uma linha através de uma mudança no dataset mesmo quando sua posição muda — essencial para o `DiffUtil` conseguir gerar animações de `move` corretas em vez de tratar um reorder como um remove seguido de um insert.

Em [`OptimizedRecyclerActivity.kt`](https://github.com/LeonardoBai12/BluetoothAndListingOptimization/blob/main/feature/listing/xml/src/main/kotlin/io/lb/bleandlistingopt/feature/listing/xml/optimized/OptimizedRecyclerActivity.kt):

```kotlin
binding.listingRecyclerView.apply {
    layoutManager = LinearLayoutManager(this@OptimizedRecyclerActivity)
    adapter = this@OptimizedRecyclerActivity.adapter
    setHasFixedSize(true)   // altura da linha não depende do conteúdo
    setItemViewCacheSize(8) // mantém algumas linhas fora da tela já bindadas
}
```

`setHasFixedSize(true)` avisa que o tamanho da própria `RecyclerView` não muda com o conteúdo do adapter — sem isso, toda `notify*` faz a `RecyclerView` remedir a si mesma "por garantia", uma passada de layout a mais que essa flag permite pular. `setItemViewCacheSize(8)` aumenta o cache de views *já vinculadas* que ficam logo fora da área visível (o padrão é 2): rolar um pouco para trás mostra uma view que ainda está com o bind antigo válido, sem pagar `onBindViewHolder` de novo imediatamente — diferente do `RecycledViewPool`, que guarda views *sem* bind, prontas só para reciclagem.

## Layout profundamente aninhado

*Problema* — [`item_listing_unoptimized.xml`](https://github.com/LeonardoBai12/BluetoothAndListingOptimization/blob/main/feature/listing/xml/src/main/res/layout/item_listing_unoptimized.xml): `LinearLayout` dentro de `LinearLayout` dentro de `FrameLayout` dentro de `LinearLayout`, e o `LinearLayout` mais interno usa `android:layout_weight="1"`. Cada nível de `ViewGroup` é uma chamada de `measure()`/`layout()` a mais que o sistema precisa percorrer para montar uma única linha — e o uso de `layout_weight` piora isso especificamente: para calcular quanto espaço cada filho com peso deve receber, o `LinearLayout` precisa medir os filhos **duas vezes** (uma vez assumindo peso zero, para saber o tamanho "natural" de cada um; uma segunda vez distribuindo o espaço restante conforme o peso) — uma passada de measure dobrada, multiplicada por quantas linhas estão visíveis, multiplicada por quantas vezes a lista é bindada.

*Solução* — [`item_listing_optimized.xml`](https://github.com/LeonardoBai12/BluetoothAndListingOptimization/blob/main/feature/listing/xml/src/main/res/layout/item_listing_optimized.xml): um único `ConstraintLayout` plano, mesma UI visual, sem `layout_weight` — todo view é filho direto, posicionado por constraints relativas às outras, então é uma única passada de measure/layout para a linha inteira, sem dobra nenhuma.

## Trabalho pesado em `onBindViewHolder`

O mesmo `formatPriceSlowly()` (uma formatação simulada como lenta, com `Thread.sleep(1)`) roda nos dois adapters — a diferença não é o trabalho em si, é a **frequência** com que ele é forçado a rodar. No `Unoptimized`, toda chamada de `updateItems()` dispara `notifyDataSetChanged()`, que rebinda todas as linhas visíveis, então `formatPriceSlowly` roda de novo para todo mundo a cada atualização, tenha o preço daquela linha mudado ou não. No `Optimized`, o `DiffUtil` já garante que `onBindViewHolder` só é chamado para linhas novas ou genuinamente alteradas — o mesmo trabalho "caro" por linha continua existindo, só que agora paga o preço uma fração das vezes.

---

[↑ Índice](./) · [Próximo: Compose (LazyColumn) →](./02-compose-lazycolumn/)
