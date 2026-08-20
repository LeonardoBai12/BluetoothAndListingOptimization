package io.lb.bleandlistingopt.feature.listing.compose.optimized

import androidx.compose.runtime.Immutable
import io.lb.bleandlistingopt.core.common.ListingItem
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/**
 * Deliberately its own type, not shared with
 * [io.lb.bleandlistingopt.feature.listing.compose.unoptimized.UnoptimizedListState]
 * even though the two look alike -- SRP: this state's reason to change is
 * "a new fix technique to demonstrate", not "a new anti-pattern to
 * demonstrate" (that's the unoptimized state's reason to change). `items`
 * is an [ImmutableList] here, not a plain `List` -- see
 * [io.lb.bleandlistingopt.feature.listing.compose.unoptimized.UnoptimizedListState]
 * for what that fixes. `@Immutable` is redundant in the strict sense (every
 * property is already a compiler-inferable-stable type), kept anyway as an
 * explicit, at-a-glance promise on the state class itself.
 */
@Immutable
data class OptimizedListState(
    val items: ImmutableList<ListingItem> = persistentListOf(),
    val query: String = "",
)

sealed interface OptimizedListEvent {
    data class OnQueryChange(val query: String) : OptimizedListEvent
    data class OnFavoriteToggle(val id: Long) : OptimizedListEvent
}

sealed interface OptimizedListEffect {
    data class ShowFavoriteToggled(val title: String) : OptimizedListEffect
}
