package com.darothub.galleria.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.darothub.galleria.model.ImageDetails


@Dao
interface ImageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(repos: List<ImageDetails>)

    @Query("SELECT * FROM images")
    fun getImages(): PagingSource<Int, ImageDetails>

    @Query("DELETE FROM images")
    suspend fun clear()
}