package com.arajangstudio.jajanhub_partner.data.remote.models

import com.squareup.moshi.JsonClass

class Facilities (
    val facility_id: String = "",
)

@JsonClass(generateAdapter = true)
data class FacilityAll(
    val id: String,
    val facility: String,
    var isSelected: Boolean
)