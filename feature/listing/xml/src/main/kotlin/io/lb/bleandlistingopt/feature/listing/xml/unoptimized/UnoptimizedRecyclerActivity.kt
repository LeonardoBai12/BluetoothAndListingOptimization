package io.lb.bleandlistingopt.feature.listing.xml.unoptimized

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import io.lb.bleandlistingopt.feature.listing.xml.databinding.ActivityUnoptimizedRecyclerBinding
import kotlinx.coroutines.launch

/**
 * ANTI-PATTERN screen -- compare against
 * [io.lb.bleandlistingopt.feature.listing.xml.optimized.OptimizedRecyclerActivity],
 * which renders the same data. To measure the difference: `adb shell
 * dumpsys gfxinfo <pkg> reset` before scrolling, scroll both screens the
 * same amount, then `adb shell dumpsys gfxinfo <pkg>` and compare "Number
 * Missed Vsync" / janky frames. For frame-by-frame detail, capture a
 * Perfetto trace (or `adb shell am start-systrace` on older devices) while
 * scrolling.
 */
class UnoptimizedRecyclerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUnoptimizedRecyclerBinding
    private val viewModel by lazy { ViewModelProvider(this)[UnoptimizedRecyclerViewModel::class.java] }
    private val adapter = UnoptimizedRecyclerAdapter(onFavoriteToggle = { id ->
        viewModel.onEvent(UnoptimizedRecyclerEvent.OnFavoriteToggle(id))
    })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUnoptimizedRecyclerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.listingRecyclerView.layoutManager = LinearLayoutManager(this)
        // ANTI-PATTERN: no setHasFixedSize(true) even though row height
        // here never changes with content -- RecyclerView will schedule an
        // extra layout pass it doesn't need to.
        binding.listingRecyclerView.adapter = adapter

        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
            override fun afterTextChanged(s: Editable?) {
                viewModel.onEvent(UnoptimizedRecyclerEvent.OnQueryChange(s?.toString().orEmpty()))
            }
        })

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state -> adapter.updateItems(state.items) }
            }
        }
    }
}
