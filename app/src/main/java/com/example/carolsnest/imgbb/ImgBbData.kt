package com.example.carolsnest.imgbb

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ImgBbResponse(
    @SerialName("data") val data: ImgBbData? = null,
    @SerialName("success") val success: Boolean,
    @SerialName("status") val status: Int? = null,
    @SerialName("error") val error: ImgBbError? = null,
)

@Serializable
data class ImgBbData(
    @SerialName("id") val id: String,
    @SerialName("url") val url: String,
    @SerialName("display_url") val displayUrl: String,
    @SerialName("delete_url") val deleteUrl: String? = null,
)

@Serializable
data class ImgBbError(
    @SerialName("message") val message: String,
    @SerialName("code") val code: Int
)