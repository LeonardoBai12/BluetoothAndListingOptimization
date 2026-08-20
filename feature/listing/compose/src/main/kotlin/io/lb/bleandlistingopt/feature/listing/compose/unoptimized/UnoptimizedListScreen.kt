package io.lb.bleandlistingopt.feature.listing.compose.unoptimized

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import io.lb.bleandlistingopt.core.common.ListingItem

/**
 * ANTI-PATTERN screen -- compare against
 * [io.lb.bleandlistingopt.feature.listing.compose.optimized.OptimizedListScreen],
 * which renders the exact same data. To see the difference: in Android
 * Studio's Layout Inspector, enable "Show recomposition counts" while this
 * screen is open, then type in the search field. Every visible row's count
 * climbs here; on the optimized screen only the rows that actually changed
 * do.
 */
@Composable
fun UnoptimizedListScreen(viewModel: UnoptimizedListViewModel = viewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    UnoptimizedListContent(items = state.items, query = state.query, onEvent = viewModel::onEvent)
}

// ANTI-PATTERN: `var` makes this class unstable to the Compose compiler --
// it can't prove the value won't change between recompositions, so every
// composable reading it is forced to recompose instead of being skipped,
// even when nothing actually changed.
private class UnstableRow(val item: ListingItem, var isFavorite: Boolean)

// ANTI-PATTERN: `items: List<ListingItem>`, matching UnoptimizedListState's
// own field type. `List` is not a stability-inferable type to the Compose
// compiler -- it could be a MutableList mutated after being passed down --
// so this parameter, and everything downstream that reads it, is treated
// as unstable, defeating recomposition skipping regardless of whether the
// list actually changed.
@Composable
private fun UnoptimizedListContent(
    items: List<ListingItem>,
    query: String,
    onEvent: (UnoptimizedListEvent) -> Unit,
) {
    Column {
        // ANTI-PATTERN: state read too high in the tree. `query` is only
        // needed by this TextField, but it's read here, in the same
        // composable scope as the LazyColumn below. Compose can't skip
        // "just the TextField" -- the whole function body re-executes on
        // every keystroke, including the list.
        TextField(
            value = query,
            onValueChange = { onEvent(UnoptimizedListEvent.OnQueryChange(it)) },
            modifier = Modifier.padding(16.dp),
        )

        // ANTI-PATTERN: no `key`, no `contentType`. Without `key`, Compose
        // identifies each row by its position in the list, so an insert,
        // removal or reorder makes it think every row after that point is "a
        // different item at the same slot" and rebuilds it from scratch
        // instead of just moving state around. Without `contentType`, rows
        // with different shapes in one list can't reuse each other's
        // composition/measurement during scroll.
        LazyColumn {
            items(items) { item ->
                val row = remember(item.id) { UnstableRow(item, item.isFavorite) }
                UnoptimizedRow(row = row, onToggle = { onEvent(UnoptimizedListEvent.OnFavoriteToggle(item.id)) })
            }
        }
    }
}

@Composable
private fun UnoptimizedRow(row: UnstableRow, onToggle: () -> Unit) {
    // ANTI-PATTERN: work that looks expensive (formatting) runs inline in
    // the composable body with no `remember` around it, so it re-runs on
    // *every* recomposition of this row, not just when the price changed.
    val formattedPrice = formatPriceSlowly(row.item.price)

    Row(modifier = Modifier.padding(12.dp)) {
        Text(row.item.title, modifier = Modifier.padding(end = 8.dp))
        Text(formattedPrice, modifier = Modifier.padding(end = 8.dp))
        Checkbox(checked = row.isFavorite, onCheckedChange = { onToggle() })
    }
}

/** Stands in for a real expensive formatter (e.g. locale-aware NumberFormat). */
private fun formatPriceSlowly(price: Double): String {
    Thread.sleep(1)
    return "$" + "%.2f".format(price)
}
