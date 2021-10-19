package com.darothub.galleria.ui

import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter

class PageLoadStateAdapter (private val retry: () -> Unit) : LoadStateAdapter<PageLoadStateViewHolder>() {
    override fun onBindViewHolder(holder: PageLoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): PageLoadStateViewHolder {
        return PageLoadStateViewHolder.create(parent, retry)
    }
}