package com.darothub.galleria.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.darothub.galleria.model.ImageDetails

@Database(
    entities = [ImageDetails::class, RemoteKeys::class],
    version = 1,
    exportSchema = false
)
abstract class ImageDatabase:RoomDatabase() {
    abstract fun imageDao(): ImageDao
    abstract fun remoteKeysDao(): RemoteKeysDao
}