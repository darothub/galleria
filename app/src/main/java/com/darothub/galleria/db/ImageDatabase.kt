package com.darothub.galleria.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.darothub.galleria.model.ImageDetails

@Database(
    entities = [ImageDetails::class, RemoteKey::class],
    version = 1,
    exportSchema = false
)
abstract class ImageDatabase:RoomDatabase() {
    abstract fun imageDao(): ImageDao
    abstract fun remoteKeysDao(): RemoteKeysDao
}