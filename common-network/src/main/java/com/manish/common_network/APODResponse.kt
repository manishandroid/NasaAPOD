package com.manish.common_network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class APODResponse(
    @Json(name = "copyright")
    var copyright: String? = null,
    @Json(name = "date")
    var date: String,
    @Json(name = "explanation")
    var explanation: String,
    @Json(name = "hdurl")
    var hdUrl: String,
    @Json(name = "media_type")
    var mediaType: String,
    @Json(name = "service_version")
    var serviceVersion: String,
    @Json(name = "title")
    var title: String,
    @Json(name = "url")
    var url: String
)
