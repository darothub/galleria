//package com.darothub.galleria.repository
//
//import androidx.paging.ExperimentalPagingApi
//import androidx.paging.Pager
//import androidx.paging.PagingConfig
//import androidx.paging.PagingData
//import com.darothub.galleria.api.ShutterImageService
//import com.darothub.galleria.data.ImageDetailsRepository
//import com.darothub.galleria.data.ImageRemoteMediator
//import com.darothub.galleria.data.PER_PAGE
//import com.darothub.galleria.db.ImageDatabase
//import com.darothub.galleria.model.ImageDetails
//import kotlinx.coroutines.flow.Flow
//
//open class FakeRepository(
//    private val service: FakeShutterService,
//    private val database: ImageDatabase
//):ImageDetailsRepository {
//    @ExperimentalPagingApi
//    override fun getImageStream(query: String): Flow<PagingData<ImageDetails>> {
//        val dbQuery = "%${query.replace(' ', '%')}%"
//        val pagingSourceFactory = { database.imageDao().getImages(dbQuery) }
//        return Pager(
//            config = PagingConfig(
//                pageSize = PER_PAGE,
//                enablePlaceholders = false,
//                maxSize = PER_PAGE + 100 * 2
//            ),
//            remoteMediator = FakeRemoteMediator(
//                query,
//                service,
//                database
//            ),
//            pagingSourceFactory = pagingSourceFactory
//        ).flow
//    }
//}