package com.darothub.galleria

import android.content.Context
import androidx.paging.ExperimentalPagingApi
import androidx.paging.RemoteMediator
import androidx.room.Room
import com.darothub.galleria.api.ShutterImageService
import com.darothub.galleria.data.ImageDetailsRepository
import com.darothub.galleria.data.ImageDetailsRepositoryImpl
import com.darothub.galleria.db.FakeRemoteMediator
import com.darothub.galleria.db.ImageDatabase
import com.darothub.galleria.model.ImageDetails
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TestAppModule {

    @Provides
    @Named("test_db")
    fun provideInMemoryDb(@ApplicationContext context: Context) =
        Room.inMemoryDatabaseBuilder(
            context, ImageDatabase::class.java
        ).allowMainThreadQueries()
            .build()
    @ExperimentalPagingApi
    @Provides
    @Named("test_remote_mediator")
    fun provideRemoteMediator():RemoteMediator<Int, ImageDetails> = FakeRemoteMediator()

}