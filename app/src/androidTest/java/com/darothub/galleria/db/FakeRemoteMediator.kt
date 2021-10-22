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
        val fakeImageDetails = listOf<ImageDetails>(
            ImageDetails("1", "url.com", "A nice dog"),
            ImageDetails("2", "url.com", "A nice cat"),
            ImageDetails("3", "cat.com", "A happy animal"),
            ImageDetails("4", "url.com", "A lion sleeping")
        )
    }
}