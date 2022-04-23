package com.arajangstudio.jajanhub_partner.data.remote.models

import com.google.gson.annotations.SerializedName

data class Merchant(
    @SerializedName("id")
    val id: String,
    @SerializedName("uuid")
    val uuid: String,
    @SerializedName("merchant_name")
    val merchant_name: String,
    @SerializedName("location_name")
    val location_name: String,
    @SerializedName("location_address")
    val location_address: String,
    @SerializedName("location_latitude")
    val location_latitude: Double,
    @SerializedName("location_longitude")
    val location_longitude: Double,
    @SerializedName("distance")
    val distance: String,
    @SerializedName("phone")
    val phone: String,
    @SerializedName("instagram")
    val instagram: String,
    @SerializedName("schedule")
    val schedule: String,
    @SerializedName("open_time")
    val open_time: String,
    @SerializedName("close_time")
    val close_time: String,
    @SerializedName("recent_open_time")
    val recent_open_time: String,
    @SerializedName("recent_close_time")
    val recent_close_time: String,
    @SerializedName("badge")
    val badge: String,
    @SerializedName("photo")
    val photo: String,
    @SerializedName("facilities")
    val facilities: List<Facility>,
    @SerializedName("menus")
    val menus: List<Menus>,
    @SerializedName("menu_photos")
    val menu_photos: List<PhotoMenu>,
    @SerializedName("rating_flavor")
    val rating_flavor: String,
    @SerializedName("rating_atmosphere")
    val rating_atmosphere: String,
    @SerializedName("rating_price_vs_flavor")
    val rating_price_vs_flavor: String,
    @SerializedName("rating_service")
    val rating_service: String,
    @SerializedName("rating_cleanliness")
    val rating_cleanliness: String,
    @SerializedName("rating_total")
    val rating_total: String,
    @SerializedName("review_total")
    val review_total: String,
    @SerializedName("complete")
    val complete: Int,
    @SerializedName("active")
    val active: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("result")
    val result: Boolean
    )