package com.darothub.galleria.ui

import Keys
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import com.darothub.galleria.R
import com.darothub.galleria.data.ImageViewModel
import com.darothub.galleria.databinding.ActivityMainBinding
import com.darothub.galleria.utils.makeFullScreen
import com.darothub.galleria.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val binding by viewBinding(ActivityMainBinding::inflate)
    val adapter by lazy { ImageListAdapter() }
    private val viewModel: ImageViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view = binding.root
        setContentView(view)
        makeFullScreen(binding.root)
        // add dividers between RecyclerView's row items
        val decoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        binding.recyclerView.addItemDecoration(decoration)

        initAdapter(true)
        Log.i("Main", Keys.toString())
        lifecycleScope.launch {
            viewModel.dataSource.collectLatest {
                adapter.submitData(it)
            }
        }

    }


    private fun initAdapter(isMediator: Boolean = false) {
        binding.recyclerView.adapter = adapter.withLoadStateHeaderAndFooter(
            header = PageLoadStateAdapter { adapter.retry() },
            footer = PageLoadStateAdapter { adapter.retry() }
        )
        adapter.addLoadStateListener { loadState ->
            val refreshState =
                if (isMediator) {
                    loadState.mediator?.refresh
                } else {
                    loadState.source.refresh
                }
            binding.recyclerView.isVisible = refreshState is LoadState.NotLoading
            binding.progressBar.isVisible = refreshState is LoadState.Loading
            binding.buttonRetry.isVisible = refreshState is LoadState.Error
            handleError(loadState)
        }
        binding.buttonRetry.setOnClickListener {
            adapter.retry()
        }
    }

    private fun handleError(loadState: CombinedLoadStates) {
        val errorState = loadState.source.append as? LoadState.Error
            ?: loadState.source.prepend as? LoadState.Error

        errorState?.let {
            Toast.makeText(this, "${it.error}", Toast.LENGTH_LONG).show()
        }
    }
}