package com.darothub.galleria.db

import androidx.annotation.VisibleForTesting
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.darothub.galleria.model.ImageDetails


@Dao
interface ImageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(images: List<ImageDetails>)

    @Query("SELECT * FROM images WHERE " +
            "url LIKE :queryString OR description LIKE :queryString ")
    fun getImages(queryString: String): PagingSource<Int, ImageDetails>

    @Query("DELETE FROM images")
    suspend fun clearImages()

    @VisibleForTesting
    @Query("SELECT * FROM images WHERE " +
            "url LIKE :queryString OR description LIKE :queryString ")
    fun getImageForTesting(queryString: String): List<ImageDetails>
}