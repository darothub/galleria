package com.darothub.galleria.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.darothub.galleria.api.ShutterImageService
import com.darothub.galleria.db.ImageDatabase
import com.darothub.galleria.model.ImageDetails
import kotlinx.coroutines.flow.Flow

interface ImageDetailsRepository {
    fun getImageStream(query: String): Flow<PagingData<ImageDetails>>
}

class ImageDetailsRepositoryImpl(
    private val service: ShutterImageService,
    private val database: ImageDatabase
) :
    ImageDetailsRepository {
    override fun getImageStream(query: String): Flow<PagingData<ImageDetails>> {
        val dbQuery = "%${query.replace(' ', '%')}%"
        val pagingSourceFactory = { database.imageDao().getImages(dbQuery) }
        return Pager(
            config = PagingConfig(
                pageSize = PER_PAGE,
                enablePlaceholders = false,
                maxSize = PER_PAGE + 100 * 2
            ),
            remoteMediator = ImageRemoteMediator(
                query,
                service,
                database
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }


}