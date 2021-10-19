package com.darothub.galleria.ui

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.darothub.galleria.model.ImageDetails

class ImageListAdapter : PagingDataAdapter<ImageDetails, RecyclerView.ViewHolder>(MODEL_COMPARATOR) {
    var list = ArrayList<ImageDetails>()
    companion object {
        private val MODEL_COMPARATOR = object : DiffUtil.ItemCallback<ImageDetails>() {
            override fun areItemsTheSame(oldItem: ImageDetails, newItem: ImageDetails): Boolean {
                return (oldItem.url == newItem.url)
            }

            override fun areContentsTheSame(oldItem: ImageDetails, newItem: ImageDetails): Boolean = oldItem == newItem
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    fun setData(newList: List<ImageDetails>) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = getItem(position)
        (holder as ImageListViewHolder).bindTo(data)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ImageListViewHolder.create(parent)
    }
}