package com.darothub.galleria.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.darothub.galleria.api.ShutterImageService
import com.darothub.galleria.db.ImageDatabase
import com.darothub.galleria.db.RemoteKey
import com.darothub.galleria.model.ImageDetails
import retrofit2.HttpException
import java.io.IOException
const val STARTING_PAGE_INDEX = 1L
const val PER_PAGE = 20


@OptIn(ExperimentalPagingApi::class)
class ImageRemoteMediator(
    private val query: String,
    private val service: ShutterImageService,
    private val imageDatabase: ImageDatabase
) : RemoteMediator<Int, ImageDetails>() {

    private val imageDao = imageDatabase.imageDao()
    private val remoteKeyDao = imageDatabase.remoteKeysDao()
    override suspend fun initialize(): InitializeAction {
        // Require that remote REFRESH is launched on initial load and succeeds before launching
        // remote PREPEND / APPEND.
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }
    override suspend fun load(loadType: LoadType, state: PagingState<Int, ImageDetails>): MediatorResult {
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


            val apiResponse = loadKey.let { service.getImages(query, it, PER_PAGE) }

            val mediaData = apiResponse.data
            val endOfPaginationReached = mediaData.isEmpty()
            val imageDetails = mediaData.map {
                ImageDetails(it.id, it.assets.preview.url, it.description)
            }

            // Store loaded data, and next key in transaction, so that
            // they're always consistent.
            imageDatabase.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    remoteKeyDao.deleteByQuery(query)
                    imageDao.clearImages()
                }
                val prevKey = if (loadKey == STARTING_PAGE_INDEX) null else loadKey.minus(1)
                val nextKey = if (endOfPaginationReached) null else loadKey.plus(1)
                // Update RemoteKey for this query.
                remoteKeyDao.insertKey(
                    RemoteKey(query, prevKey, nextKey)
                )

                imageDatabase.imageDao().insertAll(imageDetails)
            }

            MediatorResult.Success(
                endOfPaginationReached = endOfPaginationReached
            )
        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }
    }

}

