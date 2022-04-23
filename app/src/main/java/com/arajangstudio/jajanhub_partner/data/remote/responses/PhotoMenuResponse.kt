package com.arajangstudio.jajanhub_partner.data.remote.responses

import com.arajangstudio.jajanhub_partner.data.remote.models.Menus
import com.arajangstudio.jajanhub_partner.data.remote.models.PhotoMenu
import com.google.gson.annotations.SerializedName
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PhotoMenuResponse(
    @SerializedName("data") val results: ArrayList<PhotoMenu>,
)