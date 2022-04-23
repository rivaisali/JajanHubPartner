package com.arajangstudio.jajanhub_partner.data.remote.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Menus (
    val menu_id: String,
    val merchant_menu_id: String,
    val menu_category_id: String,
    val title: String,
    val category: String,
    val status: String,
    val count: Int,
    var isSelected: Boolean
)
