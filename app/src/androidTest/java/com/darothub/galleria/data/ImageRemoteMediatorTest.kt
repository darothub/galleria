package com.darothub.galleria.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.darothub.galleria.api.Assets
import com.darothub.galleria.api.MediaData
import com.darothub.galleria.api.ThumbDetails
import com.darothub.galleria.db.FakeRemoteMediator
import com.darothub.galleria.db.ImageDao
import com.darothub.galleria.db.ImageDatabase
import com.darothub.galleria.model.ImageDetails
import com.darothub.galleria.repository.FakeShutterService
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.runner.RunWith
import javax.inject.Inject
import javax.inject.Named

@ExperimentalPagingApi
@OptIn(ExperimentalCoroutinesApi::class)
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class ImageRemoteMediatorTest{
    @Inject
    @Named("test_db")
    lateinit var database: ImageDatabase
    private lateinit var imageDao: ImageDao
    @get:Rule
    var hiltRule = HiltAndroidRule(this)
    private val post = listOf<MediaData>(
        MediaData("1", Assets(preview = ThumbDetails(100L, "url.com", 100L)), "dog"),
        MediaData("2", Assets(preview = ThumbDetails(100L, "url.com", 100L)), "cat"),
        MediaData("3", Assets(preview = ThumbDetails(100L, "url.com", 100L)), "lion")
    )
    private val mockService = FakeShutterService()
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        hiltRule.inject()
        imageDao = database.imageDao()
    }

    @After
    fun tearDown() {
        database.close()
    }
    @Test
    fun refreshLoadReturnsSuccessResultWhenMoreDataIsPresent() = runBlockingTest {
        FakeShutterService.fakeApiResponse.addAll(post)
        val remoteMediator = FakeRemoteMediator("dog", service = mockService, database)
        val pagingState = PagingState<Int, ImageDetails>(
            listOf(),
            null,
            PagingConfig(1),
            1
        )
        val result = remoteMediator.load(LoadType.REFRESH, pagingState)
        assertTrue { result is RemoteMediator.MediatorResult.Success }
        assertFalse { (result as RemoteMediator.MediatorResult.Success).endOfPaginationReached }
    }
    @Test
    fun refreshLoadSuccessAndEndOfPaginationWhenNoMoreData() = runBlockingTest {
        val remoteMediator = FakeRemoteMediator("dog", service = mockService, database)
        val pagingState = PagingState<Int, ImageDetails>(
            listOf(),
            null,
            PagingConfig(1),
            1
        )
        val result = remoteMediator.load(LoadType.REFRESH, pagingState)
        assertTrue { result is RemoteMediator.MediatorResult.Success }
        assertFalse { (result as RemoteMediator.MediatorResult.Success).endOfPaginationReached }
    }
    @Test
    fun refreshLoadReturnsErrorResultWhenErrorOccurs() = runBlocking {
        // Set up failure message to throw exception from the mock API.
        mockService.failureMsg = true
        val remoteMediator = FakeRemoteMediator("dog", service = mockService, database)
        val pagingState = PagingState<Int, ImageDetails>(
            listOf(),
            null,
            PagingConfig(1),
            1
        )
        val result = remoteMediator.load(LoadType.REFRESH, pagingState)
        assertTrue {result is RemoteMediator.MediatorResult.Error }
    }
}