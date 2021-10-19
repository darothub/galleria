package com.darothub.galleria.di

import com.darothub.galleria.api.ShutterImageService
import com.darothub.galleria.data.ImageDetailsRepository
import com.darothub.galleria.data.ImageDetailsRepositoryImpl
import com.darothub.galleria.db.ImageDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Singleton
    @Provides
    fun provideRepository(
        service: ShutterImageService,
        database: ImageDatabase
    ): ImageDetailsRepository {
        return ImageDetailsRepositoryImpl(
            service, database
        )
    }
}


