package io.lb.bleandlistingopt.feature.listing.compose.unoptimized

import io.lb.bleandlistingopt.core.common.ListingItem

/**
 * Deliberately its own type, not shared with
 * [io.lb.bleandlistingopt.feature.listing.compose.optimized.OptimizedListState]
 * even though the two look alike -- SRP: this state exists to back the
 * anti-pattern screen specifically, so its reason to change is "a new
 * anti-pattern to demonstrate", not "a new fix technique to demonstrate"
 * (that's the optimized state's reason to change). `items` is a plain
 * `List` on purpose here -- see UnoptimizedListScreen for why that matters.
 */
data class UnoptimizedListState(
    val items: List<ListingItem> = emptyList(),
    val query: String = "",
)

sealed interface UnoptimizedListEvent {
    data class OnQueryChange(val query: String) : UnoptimizedListEvent
    data class OnFavoriteToggle(val id: Long) : UnoptimizedListEvent
}

sealed interface UnoptimizedListEffect {
    data class ShowFavoriteToggled(val title: String) : UnoptimizedListEffect
}
