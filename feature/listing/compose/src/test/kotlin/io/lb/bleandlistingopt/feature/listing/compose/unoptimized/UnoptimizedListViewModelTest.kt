package io.lb.bleandlistingopt.feature.listing.compose.unoptimized

import app.cash.turbine.test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/** State-reduction tests: given an event, does `state` end up where it should. */
@OptIn(ExperimentalCoroutinesApi::class)
class UnoptimizedListViewModelTest {

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `OnQueryChange filters items by title`() = runTest {
        val viewModel = UnoptimizedListViewModel()

        viewModel.state.test {
            skipItems(1) // initial, unfiltered state
            viewModel.onEvent(UnoptimizedListEvent.OnQueryChange("Item #1"))

            val filtered = awaitItem().items
            assertTrue(filtered.isNotEmpty())
            assertTrue(filtered.all { it.title.contains("Item #1") })
        }
    }

    @Test
    fun `OnFavoriteToggle flips isFavorite for that item only`() = runTest {
        val viewModel = UnoptimizedListViewModel()

        viewModel.state.test {
            val target = awaitItem().items.first { !it.isFavorite }
            viewModel.onEvent(UnoptimizedListEvent.OnFavoriteToggle(target.id))

            val updated = awaitItem().items.first { it.id == target.id }
            assertTrue(updated.isFavorite)
        }
    }
}
