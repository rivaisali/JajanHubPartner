package com.arajangstudio.jajanhub_partner.data.remote.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Schedule (
    val id: String,
    val schedule: String,
    var isSelected: Boolean
)