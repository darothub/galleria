package com.darothub.galleria.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_keys")
data class RemoteKey(
    @PrimaryKey
    val label: String,
    val prevKey: Long?,
    val nextKey: Long?
)
//
//@Entity(tableName = "posts",
//    indices = [Index(value = ["subreddit"], unique = false)])
//data class RedditPost(
//    @PrimaryKey
//    @SerializedName("name")
//    val name: String,
//    @SerializedName("title")
//    val title: String,
//    @SerializedName("score")
//    val score: Int,
//    @SerializedName("author")
//    val author: String,
//    @SerializedName("subreddit") // this seems mutable but fine for a demo
//    @ColumnInfo(collate = ColumnInfo.NOCASE)
//    val subreddit: String,
//    @SerializedName("num_comments")
//    val num_comments: Int,
//    @SerializedName("created_utc")
//    val created: Long,
//    val thumbnail: String?,
//    val url: String?) {
//    // to be consistent w/ changing backend order, we need to keep a data like this
//    var indexInResponse: Int = -1
//}

//class ListingResponse(val data: ListingData)
//
//class ListingData(
//    val children: List<RedditChildrenResponse>,
//    val after: String?,
//    val before: String?
//)
//
//data class RedditChildrenResponse(val data: RedditPost)