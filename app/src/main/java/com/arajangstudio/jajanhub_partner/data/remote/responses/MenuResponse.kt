package com.arajangstudio.jajanhub_partner.data.remote.responses

import com.arajangstudio.jajanhub_partner.data.remote.models.Menus
import com.google.gson.annotations.SerializedName
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MenuResponse(
    @SerializedName("data") val results: ArrayList<Menus>,
)