package com.darothub.galleria.di

import android.content.Context
import androidx.room.Room
import com.darothub.galleria.db.ImageDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DbModule {
    @Provides
    @Singleton
    fun provideDb(
        @ApplicationContext appContext: Context
    ): ImageDatabase {
        return Room.databaseBuilder(
            appContext, ImageDatabase::class.java,
            "Images.db"
        ).build()
    }
}