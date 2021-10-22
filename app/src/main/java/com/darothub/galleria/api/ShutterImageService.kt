package com.darothub.galleria.api

import retrofit2.http.GET
import retrofit2.http.Query

interface ShutterImageService {
    @GET("search")
    suspend fun getImages(
        @Query("query") query:String,
        @Query("page")page:Long,
        @Query("per_page")perPage:Int
    ): ApiResponse
}