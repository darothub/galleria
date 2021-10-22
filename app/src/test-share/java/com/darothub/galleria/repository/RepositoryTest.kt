//package com.darothub.galleria.repository
//
//import android.os.Build
//import android.util.Log
//import androidx.arch.core.executor.testing.InstantTaskExecutorRule
//import androidx.paging.*
//import androidx.room.Room
//import androidx.test.core.app.ApplicationProvider
//import androidx.test.ext.junit.runners.AndroidJUnit4
//import com.darothub.galleria.api.ShutterImageService
//import com.darothub.galleria.data.ImageDetailsRepository
//import com.darothub.galleria.data.ImageDetailsRepositoryImplForTest
//import com.darothub.galleria.data.ImageRemoteMediator
//import com.darothub.galleria.data.PER_PAGE
//import com.darothub.galleria.db.ImageDatabase
//import com.darothub.galleria.model.ImageDetails
//import dagger.hilt.android.testing.HiltAndroidRule
//import dagger.hilt.android.testing.HiltAndroidTest
//import junit.framework.Assert.assertEquals
//import kotlinx.coroutines.ExperimentalCoroutinesApi
//import kotlinx.coroutines.flow.*
//import kotlinx.coroutines.job
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.runBlocking
//import kotlinx.coroutines.test.runBlockingTest
//import org.junit.Before
//import org.junit.Rule
//import org.junit.Test
//import org.junit.runner.RunWith
//import org.mockito.ArgumentMatchers.any
//import org.mockito.BDDMockito.given
//import org.mockito.BDDMockito.mock
//import org.mockito.Mock
//import org.mockito.Mockito
//import org.mockito.MockitoAnnotations
//import org.mockito.MockitoAnnotations.initMocks
//import javax.inject.Inject
//import javax.inject.Named
//import org.mockito.junit.MockitoJUnit
//
//import org.mockito.junit.MockitoRule
//import org.robolectric.RobolectricTestRunner
//import org.robolectric.annotation.Config
//
//
//@RunWith(AndroidJUnit4::class)
//@Config(sdk = [Build.VERSION_CODES.P])
//class RepositoryTest {
//
//    var api: FakeShutterService = Mockito.mock(FakeShutterService::class.java)
//    lateinit var database: ImageDatabase
//    private lateinit var fakeRepository: FakeRepository
//    @get:Rule
//    var instantTaskExecutorRule = InstantTaskExecutorRule()
//
//    @ExperimentalPagingApi
//    lateinit var imageRemoteMediator: RemoteMediator<Int, ImageDetails>
//
//    @ExperimentalPagingApi
//    @Before
//    fun setUp() {
//        database = Room.inMemoryDatabaseBuilder(
//            ApplicationProvider.getApplicationContext(), ImageDatabase::class.java
//        ).allowMainThreadQueries()
//            .build()
//        fakeRepository = Mockito.spy(FakeRepository(api, database))
//    }
//
//}