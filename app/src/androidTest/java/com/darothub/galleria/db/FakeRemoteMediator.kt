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
        val fakeImageDetails = listOf(
            ImageDetails(1, "url.com", "cat on the move"),
            ImageDetails(2, "url.com", "goat on the move"),
            ImageDetails(3, "url.com", "dog on the move"),
            ImageDetails(4, "url.com", "lion on the move"),
            ImageDetails(5, "cat.com", "lion on the move"),
        )
    }
}