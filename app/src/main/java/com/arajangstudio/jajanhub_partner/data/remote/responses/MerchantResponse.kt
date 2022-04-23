package com.arajangstudio.jajanhub_partner.data.remote.responses

import com.arajangstudio.jajanhub_partner.data.remote.models.Merchant
import com.google.gson.annotations.SerializedName
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MerchantResponse(
    @SerializedName("pageCount") val pageCount: Int,
    @SerializedName("total") val total: Int,
    @SerializedName("data") val results: List<Merchant>,
    @SerializedName("currentPage") val currentPage: Int
)