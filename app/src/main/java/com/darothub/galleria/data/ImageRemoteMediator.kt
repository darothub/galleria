package com.darothub.galleria.data

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.darothub.galleria.api.ShutterImageService
import com.darothub.galleria.db.ImageDatabase
import com.darothub.galleria.db.RemoteKeys
import com.darothub.galleria.model.ImageDetails
import retrofit2.HttpException
import java.io.IOException
const val STARTING_PAGE_INDEX = 1
const val PER_PAGE = 100


interface IImageRemoteMediator {
    suspend fun getKeyPageData(loadType: LoadType, state: PagingState<Int, ImageDetails>): Any

    suspend fun getRemoteKeyForLastItem(state: PagingState<Int, ImageDetails>): RemoteKeys?

    suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, ImageDetails>): RemoteKeys?

    suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, ImageDetails>
    ): RemoteKeys?
}

@OptIn(ExperimentalPagingApi::class)
class ImageRemoteMediator(
    private val query: String,
    private val service: ShutterImageService,
    private val imageDatabase: ImageDatabase
) : RemoteMediator<Int, ImageDetails>(), IImageRemoteMediator {

    override suspend fun load(loadType: LoadType, state: PagingState<Int, ImageDetails>): MediatorResult {

        val page = when (val pageKeyData = getKeyPageData(loadType, state)) {
            is MediatorResult.Success -> {
                return pageKeyData
            }
            else -> {
                pageKeyData as Int
            }
        }

        try {
            val apiResponse = service.getImages(query = query, page = page, perPage = PER_PAGE)

            val mediaData = apiResponse.data
            val endOfPaginationReached = mediaData.isEmpty()
            val imageDetails = mediaData.map {
                ImageDetails(it.id.toLong(), it.assets.preview.url, it.description)
            }
            return imageDatabase.withTransaction {
                // clear all tables in the database
                if (loadType == LoadType.REFRESH) {
                    imageDatabase.remoteKeysDao().clearRemoteKeys()
                    imageDatabase.imageDao().clearImages()
                }
                val prevKey = if (page == STARTING_PAGE_INDEX) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1
                val keys = imageDetails.map {
                    RemoteKeys(imageId = it.id, prevKey = prevKey, nextKey = nextKey)
                }
                imageDatabase.remoteKeysDao().insertAll(keys)
                imageDatabase.imageDao().insertAll(imageDetails)
                return@withTransaction MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
            }

        } catch (exception: HttpException) {
            return MediatorResult.Error(exception)
        } catch (exception:Exception) {

            return MediatorResult.Error(exception)
        }
    }
    override suspend fun getKeyPageData(loadType: LoadType, state: PagingState<Int, ImageDetails>): Any {
        return when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: STARTING_PAGE_INDEX
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey
                return nextKey ?: MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey ?: return MediatorResult.Success(
                    endOfPaginationReached = remoteKeys != null
                )
                prevKey
            }
        }
    }
    override suspend fun getRemoteKeyForLastItem(state: PagingState<Int, ImageDetails>): RemoteKeys? {
        // Get the last page that was retrieved, that contained items.
        // From that last page, get the last item
        return state.pages.lastOrNull() { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { image ->
                // Get the remote keys of the last item retrieved
                imageDatabase.remoteKeysDao().remoteKeysImageDetailId(image.id)
            }
    }
    override suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, ImageDetails>): RemoteKeys? {
        // Get the first page that was retrieved, that contained items.
        // From that first page, get the first item
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { image ->
                // Get the remote keys of the first items retrieved
                imageDatabase.remoteKeysDao().remoteKeysImageDetailId(image.id)
            }
    }
    override suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, ImageDetails>
    ): RemoteKeys? {
        // The paging library is trying to load data after the anchor position
        // Get the item closest to the anchor position
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { imageId ->
                imageDatabase.remoteKeysDao().remoteKeysImageDetailId(imageId)
            }
        }
    }

}

