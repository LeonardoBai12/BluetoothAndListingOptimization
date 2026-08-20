package io.lb.bleandlistingopt.feature.listing.xml

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.lb.bleandlistingopt.feature.listing.xml.databinding.ItemTagChipBinding

/** Plain adapter for the small nested chip row -- not part of the lesson, just plumbing. */
class TagsAdapter(private val tags: List<String>) : RecyclerView.Adapter<TagsAdapter.TagViewHolder>() {

    class TagViewHolder(val binding: ItemTagChipBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagViewHolder {
        val binding = ItemTagChipBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TagViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TagViewHolder, position: Int) {
        holder.binding.tagText.text = tags[position]
    }

    override fun getItemCount(): Int = tags.size
}

fun tagsFor(item: io.lb.bleandlistingopt.core.common.ListingItem): List<String> = listOf(
    item.category,
    "★${item.rating}",
    if (item.isFavorite) "Favorite" else "New",
)
