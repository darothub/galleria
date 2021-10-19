package com.darothub.galleria.model

data class ApiResponse (
    val page: Long,
    val perPage: Long,
    val totalCount: Long,
    val searchID: String,
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
    val smallThumb: ThumbDetails,
    val largeThumb: ThumbDetails,
    val hugeThumb: ThumbDetails,
    val preview1000: ThumbDetails,
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