package com.github.humongouswin.insidefun.browser

import com.google.gson.annotations.SerializedName

data class VideoResponse(

	@field:SerializedName("movies")
	val movies: List<MoviesItem?>? = null,

	@field:SerializedName("lastUpdated")
	val lastUpdated: String? = null,

	@field:SerializedName("language")
	val language: String? = null,

	@field:SerializedName("providerName")
	val providerName: String? = null
)

data class VideosItem(

	@field:SerializedName("videoType")
	val videoType: String? = null,

	@field:SerializedName("bitrate")
	val bitrate: Any? = null,

	@field:SerializedName("url")
	val url: String? = null,

	@field:SerializedName("quality")
	val quality: String? = null
)

data class Content(

	@field:SerializedName("duration")
	val duration: Int? = null,

	@field:SerializedName("videos")
	val videos: List<VideosItem?>? = null,

	@field:SerializedName("dateAdded")
	val dateAdded: String? = null
)

data class MoviesItem(

	@field:SerializedName("thumbnail")
	val thumbnail: String? = null,

	@field:SerializedName("releaseDate")
	val releaseDate: String? = null,

	@field:SerializedName("genres")
	val genres: List<String?>? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("shortDescription")
	val shortDescription: String? = null,

	@field:SerializedName("title")
	val title: String? = null,

	@field:SerializedName("content")
	val content: Content? = null,

	@field:SerializedName("tags")
	val tags: List<String?>? = null
)
