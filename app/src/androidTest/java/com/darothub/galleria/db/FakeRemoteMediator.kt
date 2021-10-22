package com.darothub.galleria.db

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.darothub.galleria.api.ShutterImageService
import com.darothub.galleria.data.PER_PAGE
import com.darothub.galleria.data.STARTING_PAGE_INDEX
import com.darothub.galleria.model.ImageDetails
import com.darothub.galleria.repository.FakeShutterService
import retrofit2.HttpException
import java.io.IOException

@ExperimentalPagingApi
class FakeRemoteMediator(
    private val query: String,
    private val service: FakeShutterService,
    private val imageDatabase: ImageDatabase
) : RemoteMediator<Int, ImageDetails>(){
    private val imageDao = imageDatabase.imageDao()
    private val remoteKeyDao = imageDatabase.remoteKeysDao()

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ImageDetails>
    ): MediatorResult {
        if (service.failureMsg){
            return MediatorResult.Error(Throwable("Something went wrong"))
        }
        return try {
            val loadKey = when (loadType) {
                LoadType.REFRESH -> STARTING_PAGE_INDEX
                LoadType.PREPEND -> return MediatorResult.Success(
                    endOfPaginationReached = true
                )
                // Query remoteKeyDao for the next RemoteKey.
                LoadType.APPEND -> {
                    val remoteKey = imageDatabase.withTransaction {
                        remoteKeyDao.remoteKeyByQuery(query)
                    }
                    if (remoteKey.nextKey == null) {
                        return MediatorResult.Success(
                            endOfPaginationReached = true
                        )
                    }

                    remoteKey.nextKey
                }
            }


            val apiResponse = loadKey.let { service.getImages(query, it!!, PER_PAGE) }

            val mediaData = apiResponse.data
            val endOfPaginationReached = mediaData.isEmpty()
            val imageDetails = mediaData.map {
                ImageDetails(it.id, it.assets.preview.url, it.description)
            }


            // Store loaded data, and next key in transaction, so that
            // they're always consistent.
            if (loadType == LoadType.REFRESH) {
                remoteKeyDao.deleteByQuery(query)
                imageDao.clearImages()
            }
            val prevKey = if (loadKey == STARTING_PAGE_INDEX) null else loadKey?.minus(1)
            val nextKey = if (endOfPaginationReached) null else loadKey?.plus(1)
            // Update RemoteKey for this query.
            remoteKeyDao.insertKey(
                RemoteKey(query, prevKey, nextKey)
            )

            imageDatabase.imageDao().insertAll(imageDetails)

            MediatorResult.Success(
                endOfPaginationReached = endOfPaginationReached
            )
        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }
    }
    companion object{
        val fakeImageDetails = mutableListOf(
            ImageDetails("1", "url.com", "A nice dog"),
            ImageDetails("2", "url.com", "A nice cat"),
            ImageDetails("3", "cat.com", "A happy animal"),
            ImageDetails("4", "url.com", "A lion sleeping")
        )
        val emptyFakeImageDetails = emptyArray<ImageDetails>()
    }
}