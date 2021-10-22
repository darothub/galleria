package com.darothub.galleria.repository

import com.darothub.galleria.api.*

internal class FakeShutterService:ShutterImageService {
    companion object{
        val fakeApiResponse =  listOf(
            MediaData("1",Assets(preview = ThumbDetails(100L, "url.com", 100L)), "dog"),
            MediaData("2",Assets(preview = ThumbDetails(100L, "url.com", 100L)), "cat"),
            MediaData("3",Assets(preview = ThumbDetails(100L, "url.com", 100L)), "lion")
        )
    }

    override suspend fun getImages(query: String, page: Long, perPage: Int): ApiResponse {
        val list = fakeApiResponse.filter {
            it.assets.preview.url.contains(query) or it.description.contains(query)
        }
        return ApiResponse(
            page,
            perPage = perPage.toLong(),
            fakeApiResponse.size.toLong(),
            "id",
            list
        )
    }
}