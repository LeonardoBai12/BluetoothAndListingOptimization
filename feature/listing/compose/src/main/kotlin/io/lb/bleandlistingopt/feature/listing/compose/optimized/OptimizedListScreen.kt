package io.lb.bleandlistingopt.feature.listing.compose.optimized

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import io.lb.bleandlistingopt.core.common.ListingItem
import io.lb.bleandlistingopt.feature.listing.compose.unoptimized.UnoptimizedListScreen
import kotlinx.collections.immutable.ImmutableList

/**
 * FIX screen -- renders the same [OptimizedListState] as
 * [UnoptimizedListScreen], with every anti-pattern there fixed by name in
 * the comments below. Compare recomposition counts the same way described
 * in that file's doc comment.
 */
@Composable
fun OptimizedListScreen(viewModel: OptimizedListViewModel = viewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    OptimizedListContent(items = state.items, query = state.query, onEvent = viewModel::onEvent)
}

// FIX: `ImmutableList<ListingItem>`, matching OptimizedListState's own
// field type, instead of `List`. Unlike `List`, ImmutableList is a type the
// Compose compiler recognizes as stable on its own -- no wrapper or
// annotation needed, and no risk of a caller sneaking a mutable list in
// through a widened type.
@Composable
private fun OptimizedListContent(
    items: ImmutableList<ListingItem>,
    query: String,
    onEvent: (OptimizedListEvent) -> Unit,
) {
    val listState = rememberLazyListState()

    Column {
        // FIX: state read scoped to just this composable. `query` never
        // reaches the LazyColumn's scope, so typing only recomposes
        // SearchField, not the list below it.
        SearchField(
            query = query,
            onQueryChange = { onEvent(OptimizedListEvent.OnQueryChange(it)) },
            listState = listState,
        )
        ListingLazyColumn(
            items = items,
            listState = listState,
            onToggleFavorite = { id -> onEvent(OptimizedListEvent.OnFavoriteToggle(id)) },
        )
    }
}

@Composable
private fun SearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    listState: androidx.compose.foundation.lazy.LazyListState,
) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
            .padding(16.dp)
            // FIX: deferred state read. This lambda runs during the draw
            // phase, not composition -- reading `listState` here (instead of
            // via `by remember` in the composable body) means scrolling
            // updates the shadow without ever recomposing SearchField.
            .graphicsLayer {
                shadowElevation = if (listState.firstVisibleItemIndex > 0) 8f else 0f
            },
    )
}

@Composable
private fun ListingLazyColumn(
    items: ImmutableList<ListingItem>,
    listState: androidx.compose.foundation.lazy.LazyListState,
    onToggleFavorite: (Long) -> Unit,
) {
    // FIX: derivedStateOf. `firstVisibleItemIndex` changes on every scrolled
    // pixel, but this Boolean only changes -- and only then triggers a
    // recomposition of whatever reads it -- when the scroll position crosses
    // the threshold. Reading `listState.firstVisibleItemIndex` directly here
    // instead would recompose on every single scroll tick.
    val showScrollHint by remember { derivedStateOf { listState.firstVisibleItemIndex > 5 } }

    Box {
        // FIX: `key` lets Compose track each row by its identity instead of
        // its position, so inserts/removals/reorders move existing state
        // around instead of rebuilding everything after the change point.
        // `contentType` lets rows sharing a type reuse composition/layout
        // across scroll even if this list ever mixes row shapes.
        LazyColumn(state = listState) {
            items(items = items, key = { it.id }, contentType = { "row" }) { item ->
                OptimizedRow(item = item, onToggleFavorite = onToggleFavorite)
            }
        }
        if (showScrollHint) {
            Text("Scroll up for more", modifier = Modifier.align(Alignment.TopCenter).padding(8.dp))
        }
    }
}

// FIX: no wrapper class needed. `ListingItem` is already Compose-stable --
// every property is a `val` of a stable type -- unlike UnoptimizedRow's
// UnstableRow, which needed a `var`.
@Composable
private fun OptimizedRow(item: ListingItem, onToggleFavorite: (Long) -> Unit) {
    // FIX: hoisted, stable lambda -- remembered per item.id instead of a
    // fresh closure allocated on every recomposition of this row.
    val onClick = remember(item.id) { { onToggleFavorite(item.id) } }

    // FIX: remembered against the value it derives from, so it only
    // re-runs when the price actually changes, not on every recomposition.
    val formattedPrice = remember(item.price) { formatPriceSlowly(item.price) }

    Row(modifier = Modifier.padding(12.dp)) {
        Text(item.title, modifier = Modifier.padding(end = 8.dp))
        Text(formattedPrice, modifier = Modifier.padding(end = 8.dp))
        Checkbox(checked = item.isFavorite, onCheckedChange = { onClick() })
    }
}

private fun formatPriceSlowly(price: Double): String {
    Thread.sleep(1)
    return "$" + "%.2f".format(price)
}
