package com.darothub.galleria.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.*
import androidx.test.ext.junit.runners.AndroidJUnit4
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
    private val post = FakeShutterService.fakeApiResponse
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
        val remoteMediator = ImageRemoteMediator("dog", service = mockService, database)
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
}