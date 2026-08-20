package io.lb.bleandlistingopt.feature.listing.xml.optimized

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.lb.bleandlistingopt.core.common.FakeListingDataGenerator
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OptimizedRecyclerViewModel : ViewModel() {
    private val allItems = FakeListingDataGenerator.generate()
    private val query = MutableStateFlow("")
    private val favoriteIds = MutableStateFlow(allItems.filter { it.isFavorite }.map { it.id }.toSet())

    val state: StateFlow<OptimizedRecyclerState> = combine(query, favoriteIds) { currentQuery, favorites ->
        val filtered = if (currentQuery.isBlank()) {
            allItems
        } else {
            allItems.filter { it.title.contains(currentQuery, ignoreCase = true) }
        }
        OptimizedRecyclerState(
            items = filtered.map { it.copy(isFavorite = it.id in favorites) },
            query = currentQuery,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
        initialValue = OptimizedRecyclerState(items = allItems),
    )

    private val _effects = MutableSharedFlow<OptimizedRecyclerEffect>()
    val effects: SharedFlow<OptimizedRecyclerEffect> = _effects.asSharedFlow()

    fun onEvent(event: OptimizedRecyclerEvent) {
        when (event) {
            is OptimizedRecyclerEvent.OnQueryChange -> query.value = event.query
            is OptimizedRecyclerEvent.OnFavoriteToggle -> toggleFavorite(event.id)
        }
    }

    private fun toggleFavorite(id: Long) {
        favoriteIds.update { current -> if (id in current) current - id else current + id }
        viewModelScope.launch {
            val item = allItems.first { it.id == id }
            _effects.emit(OptimizedRecyclerEffect.ShowFavoriteToggled(item.title))
        }
    }

    private companion object {
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
