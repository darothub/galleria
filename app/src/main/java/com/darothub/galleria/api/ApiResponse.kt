package com.darothub.galleria.model

import com.google.gson.annotations.SerializedName

data class ApiResponse (
    val page: Long,
    @SerializedName("per_page")
    val perPage: Long,
    @SerializedName("total_count")
    val totalCount: Long,
    @SerializedName("search_id")
    val searchId: String,
    val data: List<MediaData>,
    val spellcheckInfo: SpellcheckInfo
)

data class MediaData (
    val id: String,
    val assets: Assets,
    val contributor: Contributor,
    val description: String,
    val imageType: String,
    val hasModelRelease: Boolean,
    val mediaType: String
)

data class Assets (
    val preview: ThumbDetails,
    @SerializedName("small_thumb")
    val smallThumb: ThumbDetails,
    @SerializedName("large_thumb")
    val largeThumb: ThumbDetails,
    @SerializedName("huge_thumb")
    val hugeThumb: ThumbDetails,
    @SerializedName("preview_1000")
    val preview1000: ThumbDetails,
    @SerializedName("preview_1500")
    val preview1500: ThumbDetails
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