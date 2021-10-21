package com.darothub.galleria.db

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.ExperimentalPagingApi

import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject
import javax.inject.Named


@HiltAndroidTest
class ImageDaoTest {
    @Inject
    @Named("test_db")
    lateinit var database: ImageDatabase
    private lateinit var imageDao: ImageDao
    @get:Rule
    var hiltRule = HiltAndroidRule(this)
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

    @ExperimentalPagingApi
    @ExperimentalCoroutinesApi
    @Test
    fun insertImages() = runBlockingTest {
        imageDao.insertAll(FakeRemoteMediator.fakeImageDetails)
        val res = imageDao.getImageForTesting("%dog%")
        assert(res.size == 1)

    }
    @ExperimentalPagingApi
    @ExperimentalCoroutinesApi
    @Test
    fun given_a_query_returns_related_data_to_the_query() = runBlockingTest {
        imageDao.insertAll(FakeRemoteMediator.fakeImageDetails)
        val res = imageDao.getImageForTesting("%cat%")
        assert(res.size == 2)
    }
    @ExperimentalPagingApi
    @ExperimentalCoroutinesApi
    @Test
    fun clearDB() = runBlockingTest {
        imageDao.insertAll(FakeRemoteMediator.fakeImageDetails)
        imageDao.clearImages()
        val res = imageDao.getImageForTesting("%cat%")
        assert(res.isEmpty())
    }
}