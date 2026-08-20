package io.lb.bleandlistingopt.feature.listing.xml.unoptimized

import io.lb.bleandlistingopt.core.common.ListingItem

/**
 * Deliberately its own type, not shared with
 * [io.lb.bleandlistingopt.feature.listing.xml.optimized.OptimizedRecyclerState]
 * even though the two look alike -- SRP: this state's reason to change is
 * "a new RecyclerView anti-pattern to demonstrate", not "a new fix
 * technique to demonstrate" (that's the optimized state's reason to change).
 */
data class UnoptimizedRecyclerState(
    val items: List<ListingItem> = emptyList(),
    val query: String = "",
)

sealed interface UnoptimizedRecyclerEvent {
    data class OnQueryChange(val query: String) : UnoptimizedRecyclerEvent
    data class OnFavoriteToggle(val id: Long) : UnoptimizedRecyclerEvent
}

sealed interface UnoptimizedRecyclerEffect {
    data class ShowFavoriteToggled(val title: String) : UnoptimizedRecyclerEffect
}
