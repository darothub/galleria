package com.darothub.galleria.repository

import com.darothub.galleria.api.*

class FakeShutterService:ShutterImageService {
    var failureMsg = false
    companion object{
        val fakeApiResponse =  mutableListOf<MediaData>()
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