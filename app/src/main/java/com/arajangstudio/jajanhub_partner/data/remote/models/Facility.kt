package com.arajangstudio.jajanhub_partner.data.remote.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Facility(
    val facility_id: String,
    val merchant_uuid: String,
    val facility: String,
    var isSelected: Boolean
)