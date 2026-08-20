package io.lb.bleandlistingopt.feature.listing.xml.optimized

import io.lb.bleandlistingopt.core.common.ListingItem

/**
 * Deliberately its own type, not shared with
 * [io.lb.bleandlistingopt.feature.listing.xml.unoptimized.UnoptimizedRecyclerState]
 * even though the two look alike -- SRP: this state's reason to change is
 * "a new RecyclerView fix technique to demonstrate", not "a new
 * anti-pattern to demonstrate" (that's the unoptimized state's reason to
 * change).
 */
data class OptimizedRecyclerState(
    val items: List<ListingItem> = emptyList(),
    val query: String = "",
)

sealed interface OptimizedRecyclerEvent {
    data class OnQueryChange(val query: String) : OptimizedRecyclerEvent
    data class OnFavoriteToggle(val id: Long) : OptimizedRecyclerEvent
}

sealed interface OptimizedRecyclerEffect {
    data class ShowFavoriteToggled(val title: String) : OptimizedRecyclerEffect
}
