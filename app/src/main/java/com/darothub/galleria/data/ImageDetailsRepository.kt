package com.darothub.galleria.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.darothub.galleria.api.ShutterImageService
import com.darothub.galleria.db.ImageDatabase
import com.darothub.galleria.model.ImageDetails
import kotlinx.coroutines.flow.Flow

interface ImageDetailsRepository {
    fun getImageStream(query: String="empty"): Flow<PagingData<ImageDetails>>
}

class ImageDetailsRepositoryImpl(
    private val service: ShutterImageService,
    private val database: ImageDatabase
) :
    ImageDetailsRepository {
    override fun getImageStream(query: String): Flow<PagingData<ImageDetails>> {
//        val dbQuery = "%${query.replace(' ', '%')}%"
        val pagingSourceFactory = { database.imageDao().getImages() }
        return Pager(
            config = PagingConfig(
                pageSize = PER_PAGE,
                maxSize = PER_PAGE + (PER_PAGE * 2),
                enablePlaceholders = false
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