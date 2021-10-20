package com.darothub.galleria.ui

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.darothub.galleria.data.UiModel
import com.darothub.galleria.model.ImageDetails

class ImageListAdapter : PagingDataAdapter<UiModel.ImageItem, RecyclerView.ViewHolder>(MODEL_COMPARATOR) {
    var list = ArrayList<ImageDetails>()

    companion object {
        private val MODEL_COMPARATOR = object : DiffUtil.ItemCallback<UiModel.ImageItem>() {
            override fun areItemsTheSame(oldItem: UiModel.ImageItem, newItem: UiModel.ImageItem): Boolean {
                return (
                        oldItem.image.url == newItem.image.url)
            }

            override fun areContentsTheSame(oldItem: UiModel.ImageItem, newItem: UiModel.ImageItem): Boolean =
                oldItem == newItem
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val uiModel = getItem(position)

        uiModel.let {
            when (uiModel) {
                is UiModel.ImageItem -> (holder as ImageListViewHolder).bindTo(uiModel.image)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ImageListViewHolder.create(parent)
    }
}