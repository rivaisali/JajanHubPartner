package com.arajangstudio.jajanhub_partner.data.remote.responses

import com.arajangstudio.jajanhub_partner.data.remote.models.FacilityAll
import com.google.gson.annotations.SerializedName
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FacilityResponse(
    @SerializedName("data") val results: ArrayList<FacilityAll>,
)