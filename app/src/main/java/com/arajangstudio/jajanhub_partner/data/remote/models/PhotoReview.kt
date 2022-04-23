package com.arajangstudio.jajanhub_partner.data.remote.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PhotoReview(
    val id: String,
    val review_uuid: String,
    val photo: String,

)