package com.darothub.galleria.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.darothub.galleria.data.ImageViewModel
import com.darothub.galleria.data.UiAction
import com.darothub.galleria.data.UiState
import com.darothub.galleria.databinding.ActivityMainBinding
import com.darothub.galleria.utils.makeFullScreen
import com.darothub.galleria.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val binding by viewBinding(ActivityMainBinding::inflate)

    private val viewModel: ImageViewModel by viewModels()
    val adapter by lazy { ImageListAdapter() }
    @InternalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view = binding.root
        setContentView(view)
        makeFullScreen(binding.root)
        // add dividers between RecyclerView's row items
        val decoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        binding.recyclerView.addItemDecoration(decoration)

        initAdapter(viewModel.state, viewModel.accept)

    }


    @InternalCoroutinesApi
    private fun initAdapter(
        uiState: StateFlow<UiState>,
        uiActions: (UiAction) -> Unit
    ) {

        binding.recyclerView.adapter = adapter.withLoadStateHeaderAndFooter(
            header = PageLoadStateAdapter { adapter.retry() },
            footer = PageLoadStateAdapter { adapter.retry() }
        )
        makeSearch(uiState, uiActions)
        makeList(
            PageLoadStateAdapter { adapter.retry() },
            imageListAdapter = adapter,
            uiState = uiState,
            onScrollChanged = uiActions
        )

    }

    @InternalCoroutinesApi
    private fun makeSearch(
        uiState: StateFlow<UiState>,
        onQueryChanged: (UiAction.Search) -> Unit
    ) {

        binding.searchImage.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
                updateImageListFromInput(onQueryChanged)
                true
            } else {
                false
            }
        }
        binding.searchImage.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                updateImageListFromInput(onQueryChanged)
                true
            } else {
                false
            }
        }

        lifecycleScope.launch {
            uiState
                .map { it.query }
                .distinctUntilChanged()
                .collect(binding.searchImage::setText)
        }
    }

    private fun makeList(
        header: PageLoadStateAdapter,
        imageListAdapter: ImageListAdapter,
        uiState: StateFlow<UiState>,
        onScrollChanged: (UiAction.Scroll) -> Unit
    ) {
        binding.buttonRetry.setOnClickListener { imageListAdapter.retry() }
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy != 0) onScrollChanged(UiAction.Scroll(currentQuery = uiState.value.query))
            }
        })

        val notLoading = imageListAdapter.loadStateFlow
            // Only emit when REFRESH LoadState for RemoteMediator changes.
            .distinctUntilChangedBy { it.refresh }
            // Only react to cases where Remote REFRESH completes i.e., NotLoading.
            .map { it.refresh is LoadState.NotLoading }

        val hasNotScrolledForCurrentSearch = uiState
            .map { it.hasNotScrolledForCurrentSearch }
            .distinctUntilChanged()

        val shouldScrollToTop = combine(
            notLoading,
            hasNotScrolledForCurrentSearch,
            Boolean::and
        )
            .distinctUntilChanged()

        val pagingData = uiState
            .map { it.pagingData }
            .distinctUntilChanged()

        lifecycleScope.launch {
            combine(shouldScrollToTop, pagingData, ::Pair)
                // Each unique PagingData should be submitted once, take the latest from
                // shouldScrollToTop
                .distinctUntilChangedBy { it.second }
                .collectLatest { (shouldScroll, pagingData) ->
                    imageListAdapter.submitData(pagingData)
                    // Scroll only after the data has been submitted to the adapter,
                    // and is a fresh search
                    if (shouldScroll) binding.recyclerView.scrollToPosition(0)
                }
        }
        lifecycleScope.launch {
            imageListAdapter.loadStateFlow.collect { loadState ->
                // Show a retry header if there was an error refreshing, and items were previously
                // cached OR default to the default prepend state
                header.loadState = loadState.mediator
                    ?.refresh
                    ?.takeIf { it is LoadState.Error && imageListAdapter.itemCount > 0 }
                    ?: loadState.prepend
                val isListEmpty =
                    loadState.refresh is LoadState.NotLoading && imageListAdapter.itemCount == 0
                // show empty list
                binding.emptyList.isVisible = isListEmpty
                // Only show the list if refresh succeeds.
                binding.recyclerView.isVisible = !isListEmpty
                // Show loading spinner during initial load or refresh.
                binding.progressBar.isVisible = loadState.source.refresh is LoadState.Loading
                // Show the retry state if initial load or refresh fails.
                binding.buttonRetry.isVisible = loadState.source.refresh is LoadState.Error

                // Toast on any error, regardless of whether it came from RemoteMediator or PagingSource
                handleError(loadState)
            }
        }
    }

    private fun updateImageListFromInput(onQueryChanged: (UiAction.Search) -> Unit) {
        binding.searchImage.text.trim().let {
            if (it.isNotEmpty()) {
                onQueryChanged(UiAction.Search(query = it.toString()))
            }
        }
    }

    private fun handleError(loadState: CombinedLoadStates) {
        val errorState = loadState.source.append as? LoadState.Error
            ?: loadState.source.prepend as? LoadState.Error
            ?: loadState.append as? LoadState.Error
            ?: loadState.prepend as? LoadState.Error
        errorState?.let {
            Toast.makeText(
                this@MainActivity,
                "\uD83D\uDE28 Wooops ${it.error}",
                Toast.LENGTH_LONG
            ).show()
        }
    }

}