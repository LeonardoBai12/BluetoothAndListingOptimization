package io.lb.bleandlistingopt.feature.listing.xml.optimized

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import io.lb.bleandlistingopt.core.common.ListingItem
import io.lb.bleandlistingopt.feature.listing.xml.TagsAdapter
import io.lb.bleandlistingopt.feature.listing.xml.databinding.ItemListingOptimizedBinding
import io.lb.bleandlistingopt.feature.listing.xml.tagsFor

private const val PAYLOAD_FAVORITE = "favorite"

// FIX: DiffUtil.ItemCallback. `areItemsTheSame` identifies a row across
// updates by id, independent of position; `areContentsTheSame` decides
// whether a matched row needs any rebind at all; `getChangePayload` lets a
// matched-but-changed row get a *partial* rebind instead of a full one --
// ListAdapter's AsyncListDiffer calls notifyItemChanged(position, payload)
// for it automatically, which is what onBindViewHolder's payloads parameter
// below receives.
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

/**
 * FIX adapter -- same visual output as
 * [io.lb.bleandlistingopt.feature.listing.xml.unoptimized.UnoptimizedRecyclerAdapter],
 * every anti-pattern there fixed.
 */
class OptimizedRecyclerAdapter(
    private val sharedTagsPool: RecyclerView.RecycledViewPool,
    private val onFavoriteToggle: (Long) -> Unit,
) : ListAdapter<ListingItem, OptimizedRecyclerAdapter.ViewHolder>(ListingDiffCallback) {

    // FIX: stable ids. Combined with setHasStableIds(true) in the Activity,
    // this lets RecyclerView track a row's identity across a dataset change
    // even when its position moves, instead of assuming "position N is
    // still the same view" (the default, and wrong once rows reorder).
    init {
        setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long = getItem(position).id

    class ViewHolder(val binding: ItemListingOptimizedBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemListingOptimizedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        // FIX: every row's nested RecyclerView shares one pool, so a
        // tag-chip view scrolled off row N is reusable by row N+1 instead of
        // being re-inflated.
        binding.tagsRecyclerView.setRecycledViewPool(sharedTagsPool)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.contains(PAYLOAD_FAVORITE)) {
            // FIX: partial bind. Only the checkbox changes for a favorite
            // toggle, so only the checkbox is touched -- title/price/tags
            // are left exactly as they were, no re-inflation of anything.
            bindFavorite(holder, getItem(position))
            return
        }
        onBindViewHolder(holder, position)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        val binding = holder.binding

        // FIX: minimal work in onBindViewHolder -- DiffUtil already
        // guarantees this only runs for rows that are new or genuinely
        // changed, not the whole list on every update.
        binding.titleText.text = item.title
        binding.subtitleText.text = item.subtitle
        binding.priceText.text = formatPriceSlowly(item.price)
        bindFavorite(holder, item)
        binding.tagsRecyclerView.adapter = TagsAdapter(tagsFor(item))
    }

    private fun bindFavorite(holder: ViewHolder, item: ListingItem) {
        val checkbox = holder.binding.favoriteCheckbox
        checkbox.setOnCheckedChangeListener(null)
        checkbox.isChecked = item.isFavorite
        checkbox.setOnCheckedChangeListener { _, _ -> onFavoriteToggle(item.id) }
    }
}

private fun formatPriceSlowly(price: Double): String {
    Thread.sleep(1)
    return "$" + "%.2f".format(price)
}
