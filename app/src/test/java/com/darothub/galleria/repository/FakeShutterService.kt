package com.darothub.galleria.repository

import com.darothub.galleria.api.*
import com.darothub.galleria.model.ImageDetails

open class FakeShutterService:ShutterImageService {
    override suspend fun getImages(query: String, page: Int, perPage: Int): ApiResponse {
        val list = fakeImageDetails.filter {
            it.assets.preview.url.contains(query) or it.description.contains(query)
        }
        return ApiResponse(
            page.toLong(),
            perPage = perPage.toLong(),
            fakeImageDetails.size.toLong(),
            "id",
           list
        )
    }

    companion object{
        val fakeImageDetails =  listOf(
            MediaData("1",Assets(preview = ThumbDetails(100L, "url.com", 100L)), "dog"),
            MediaData("2",Assets(preview = ThumbDetails(100L, "url.com", 100L)), "cat"),
            MediaData("3",Assets(preview = ThumbDetails(100L, "url.com", 100L)), "lion")
        )
    }
}