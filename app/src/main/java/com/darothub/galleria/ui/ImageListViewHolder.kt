package com.darothub.galleria.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.darothub.galleria.R
import com.darothub.galleria.databinding.PhotoListItemBinding
import com.darothub.galleria.model.ImageDetails

class ImageListViewHolder(private val binding: PhotoListItemBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bindTo(imageDetails: ImageDetails?) {
        binding.itemImage.load(imageDetails?.url){
            crossfade(true)
            placeholder(R.drawable.abc_vector_test)
        }
        binding.itemTitle.text = imageDetails?.description
    }
    companion object {
        fun create(parent: ViewGroup): ImageListViewHolder {
            val binding = PhotoListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return ImageListViewHolder(binding)
        }
    }
}