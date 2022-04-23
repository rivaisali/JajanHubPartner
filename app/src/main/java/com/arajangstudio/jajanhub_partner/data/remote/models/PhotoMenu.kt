package com.arajangstudio.jajanhub_partner.data.remote.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PhotoMenu(
    val id: String,
    val merchant_uuid: String,
    val photo: String,

    )