package com.darothub.galleria.api

import com.google.gson.annotations.SerializedName

data class ApiResponse (
    val page: Long,
    @SerializedName("per_page")
    val perPage: Long,
    @SerializedName("total_count")
    val totalCount: Long,
    @SerializedName("search_id")
    val searchId: String,
    @SerializedName("data")
    val data: List<MediaData>,
)

data class MediaData (
    val id: String,
    val assets: Assets,
    @SerializedName("description")
    val description: String,
)

data class Assets (
    @SerializedName("preview")
    val preview: ThumbDetails,
)

data class ThumbDetails (
    val height: Long,
    val url: String,
    val width: Long
)
data class Contributor (
    val id: String
)
class SpellcheckInfo()