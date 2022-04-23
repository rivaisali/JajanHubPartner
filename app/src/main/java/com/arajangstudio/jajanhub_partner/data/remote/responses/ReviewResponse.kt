package com.arajangstudio.jajanhub_partner.data.remote.responses

import com.arajangstudio.jajanhub_partner.data.remote.models.Review
import com.google.gson.annotations.SerializedName
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ReviewResponse(
    @SerializedName("pageCount") val pageCount: Int,
    @SerializedName("total") val total: Int,
    @SerializedName("data") val results: List<Review>,
    @SerializedName("currentPage") val currentPage: Int
)