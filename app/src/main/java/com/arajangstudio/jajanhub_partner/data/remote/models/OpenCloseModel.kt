package com.arajangstudio.jajanhub_partner.data.remote.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class OpenCloseModel(
    val status: String,
    val time: String,
    var message: String
)