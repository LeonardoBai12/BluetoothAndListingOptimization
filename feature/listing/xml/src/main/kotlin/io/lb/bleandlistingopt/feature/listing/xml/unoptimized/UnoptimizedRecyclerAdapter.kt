package io.lb.bleandlistingopt.feature.listing.xml.unoptimized

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.lb.bleandlistingopt.core.common.ListingItem
import io.lb.bleandlistingopt.feature.listing.xml.TagsAdapter
import io.lb.bleandlistingopt.feature.listing.xml.databinding.ItemListingUnoptimizedBinding
import io.lb.bleandlistingopt.feature.listing.xml.tagsFor

/**
 * ANTI-PATTERN adapter -- compare against
 * [io.lb.bleandlistingopt.feature.listing.xml.optimized.OptimizedRecyclerAdapter].
 */
class UnoptimizedRecyclerAdapter(
    private val onFavoriteToggle: (Long) -> Unit,
) : RecyclerView.Adapter<UnoptimizedRecyclerAdapter.ViewHolder>() {

    private var items: List<ListingItem> = emptyList()

    // ANTI-PATTERN: no DiffUtil, just notifyDataSetChanged(). Every call
    // rebinds every visible row from scratch, even if only one item's
    // favorite flag changed -- there's no way for RecyclerView to know what
    // actually differs.
    fun updateItems(newItems: List<ListingItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    class ViewHolder(val binding: ItemListingUnoptimizedBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemListingUnoptimizedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val binding = holder.binding

        binding.titleText.text = item.title
        binding.subtitleText.text = item.subtitle
        // ANTI-PATTERN: heavy work directly in onBindViewHolder. This runs
        // synchronously on the main thread for every row bound while
        // scrolling -- with real formatting/parsing work (not this
        // simulated sleep) this is exactly the kind of per-frame cost that
        // shows up as dropped frames in `dumpsys gfxinfo`.
        binding.priceText.text = formatPriceSlowly(item.price)
        binding.favoriteCheckbox.setOnCheckedChangeListener(null)
        binding.favoriteCheckbox.isChecked = item.isFavorite
        binding.favoriteCheckbox.setOnCheckedChangeListener { _, _ -> onFavoriteToggle(item.id) }

        // ANTI-PATTERN: a fresh adapter (and implicitly a fresh
        // RecycledViewPool) on every bind, with no pool shared across rows.
        // Row N+1 can never reuse the chip views row N already inflated.
        binding.tagsRecyclerView.adapter = TagsAdapter(tagsFor(item))

        // ANTI-PATTERN: setIsRecyclable(false) tells RecyclerView this
        // holder must never go back into the recycle pool. With this set,
        // every row that scrolls off screen is discarded outright instead
        // of being reused, which floods the system with fresh inflations
        // for rows scrolling back into view -- the exact opposite of what
        // RecyclerView (recycler + view) exists to avoid.
        holder.setIsRecyclable(false)
    }

    override fun getItemCount(): Int = items.size
}

private fun formatPriceSlowly(price: Double): String {
    Thread.sleep(1)
    return "$" + "%.2f".format(price)
}
