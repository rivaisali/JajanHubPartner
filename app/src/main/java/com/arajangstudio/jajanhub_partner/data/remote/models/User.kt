package com.arajangstudio.jajanhub_partner.data.remote.models

import com.google.gson.annotations.SerializedName
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class User(
    @SerializedName(" id")
    val user_id: String,
    @SerializedName("full_name")
    val full_name: String,
    @SerializedName("phone")
    val phone: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("photo")
    val photo: String,
    @SerializedName("status")
    val status: String
)