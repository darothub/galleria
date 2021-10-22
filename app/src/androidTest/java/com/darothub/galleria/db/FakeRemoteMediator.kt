package com.darothub.galleria.db

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.darothub.galleria.model.ImageDetails

@ExperimentalPagingApi
class FakeRemoteMediator : RemoteMediator<Int, ImageDetails>(){
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ImageDetails>
    ): MediatorResult {
        TODO("Not yet implemented")
    }
    companion object{
        val fakeImageDetails = emptyList<ImageDetails>()
    }
}