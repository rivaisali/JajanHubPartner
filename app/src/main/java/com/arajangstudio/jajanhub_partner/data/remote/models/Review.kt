package com.arajangstudio.jajanhub_partner.data.remote.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Review(
    val id: String,
    val user_id: String,
    val merchant_uuid: String,
    val full_name: String,
    val message: String,
    val merchant_name: String,
    val total_review: String,
    val total_rating: String,
    val photos: List<PhotoReview>

)