package com.darothub.galleria.data

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ImageViewModel @Inject constructor(
    private val repository: ImageDetailsRepository,
    private val savedStateHandle: SavedStateHandle
):ViewModel() {

    val dataSource = repository.getImageStream()

}