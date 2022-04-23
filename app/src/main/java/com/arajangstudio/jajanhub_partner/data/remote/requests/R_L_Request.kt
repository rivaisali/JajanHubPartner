package com.arajangstudio.jajanhub_partner.data.remote.requests

import com.squareup.moshi.JsonClass
import retrofit2.http.Query

@JsonClass(generateAdapter = true)
data class UserRequest(
    val full_name:String,
    val phone:String,
    val email:String,
    val password:String,
    val password_confirm:String,
    val uuid:String,
    val role:String
)

@JsonClass(generateAdapter = true)
data class LocationRequest(
    val lastLocation:String,
)

@JsonClass(generateAdapter = true)
data class TokenRequest(
    val token:String,
)

@JsonClass(generateAdapter = true)
data class MerchantRequest(
    val user_uid:String,
    val type_merchant:String,
    val merchant_name:String,
    val branch: Int,
    val phone: String,
    val instagram: String,
    val location_address: String,
    val location_name: String,
    val location_latitude: Double,
    val location_longitude: Double,
    val schedule: String,
    val open_time:String,
    val close_time: String,
    val photo: String,
)

@JsonClass(generateAdapter = true)
data class MerchantMenuRequest(
    val menus:String,
    val facilities: String,
    val photos:String,
)

@JsonClass(generateAdapter = true)
data class CreateMenuRequest(
    val merchant_uuid:String,
    val menu_id: String,
)

@JsonClass(generateAdapter = true)
data class CreateFacilityRequest(
    val merchant_uuid:String,
    val facilities: String,
)

@JsonClass(generateAdapter = true)
data class UpdateMerchantRequest(
    val merchant_uuid:String,
    val photo:String
)

@JsonClass(generateAdapter = true)
data class UpdatePhotoMenuRequest(
    val id:String,
    val photo:String
)


@JsonClass(generateAdapter = true)
data class CreateSuggestionRequest(
    val user_uuid:String,
    val title:String,
    val message:String
)

@JsonClass(generateAdapter = true)
data class MerchantStatusRequest(
    val status:String,
)

@JsonClass(generateAdapter = true)
data class MerchantsListRequest(
    val page:Query
)

