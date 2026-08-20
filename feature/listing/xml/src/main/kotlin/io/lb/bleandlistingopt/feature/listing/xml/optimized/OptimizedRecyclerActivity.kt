package io.lb.bleandlistingopt.feature.listing.xml.optimized

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.lb.bleandlistingopt.feature.listing.xml.databinding.ActivityOptimizedRecyclerBinding
import kotlinx.coroutines.launch

class OptimizedRecyclerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOptimizedRecyclerBinding
    private val viewModel by lazy { ViewModelProvider(this)[OptimizedRecyclerViewModel::class.java] }

    // FIX: one pool shared by every row's nested tags RecyclerView, handed
    // to the adapter instead of each row building its own.
    private val sharedTagsPool = RecyclerView.RecycledViewPool()
    private val adapter = OptimizedRecyclerAdapter(
        sharedTagsPool = sharedTagsPool,
        onFavoriteToggle = { id -> viewModel.onEvent(OptimizedRecyclerEvent.OnFavoriteToggle(id)) },
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOptimizedRecyclerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.listingRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@OptimizedRecyclerActivity)
            adapter = this@OptimizedRecyclerActivity.adapter
            // FIX: row height doesn't depend on content, so RecyclerView can
            // skip a layout pass it would otherwise redo defensively.
            setHasFixedSize(true)
            // FIX: keeps a few extra off-screen rows fully bound (not just
            // pooled), so a small scroll-back shows an already-bound view
            // instead of paying onBindViewHolder again immediately.
            setItemViewCacheSize(8)
        }

        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
            override fun afterTextChanged(s: Editable?) {
                viewModel.onEvent(OptimizedRecyclerEvent.OnQueryChange(s?.toString().orEmpty()))
            }
        })

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // FIX: ListAdapter.submitList runs the DiffUtil diff on a
                // background thread and dispatches only the granular
                // insert/remove/change calls the diff found -- no
                // notifyDataSetChanged() anywhere in this path.
                viewModel.state.collect { state -> adapter.submitList(state.items) }
            }
        }
    }
}
