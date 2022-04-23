package com.arajangstudio.jajanhub_partner.data.remote

import com.arajangstudio.jajanhub_partner.BuildConfig
import com.arajangstudio.jajanhub_partner.data.remote.models.Merchant
import com.arajangstudio.jajanhub_partner.data.remote.models.OpenCloseModel
import com.arajangstudio.jajanhub_partner.data.remote.models.PhotoMenu
import com.arajangstudio.jajanhub_partner.data.remote.models.User
import com.arajangstudio.jajanhub_partner.data.remote.requests.*
import com.arajangstudio.jajanhub_partner.data.remote.responses.*
import retrofit2.Response
import retrofit2.http.*

interface ApiServices {


    @POST("auth/register?api_key=${BuildConfig.API_KEY}")
    suspend fun register(
        @Body userRequest: UserRequest
    ):Response<User>

    @POST("user/last_location/uid/{uuid}/role/3?api_key=${BuildConfig.API_KEY}")
    suspend fun updateLastLocation(
        @Path("uuid") uuid:String,
        @Body locationRequest: LocationRequest
    ):Response<User>

    @POST("user/token/uid/{uuid}/role/3?api_key=${BuildConfig.API_KEY}")
    suspend fun updateToken(
        @Path("uuid") uuid:String,
        @Body tokenRequest: TokenRequest
    ):Response<User>

    @GET("user/validate/uid/{uuid}/role/3?api_key=${BuildConfig.API_KEY}")
    suspend fun validate(
        @Path("uuid") uuid:String,
    ): Response<User>

    @GET("menus?api_key=${BuildConfig.API_KEY}")
    suspend fun menuList(): Response<MenuResponse>

    @GET("menu/merchant/{merchant_uuid}/list?api_key=${BuildConfig.API_KEY}")
    suspend fun menuListMerchant(
        @Path("merchant_uuid") merchant_uuid:String,
    ): Response<MenuResponse>

    @GET("facilities?api_key=${BuildConfig.API_KEY}")
    suspend fun facilitiesList(): Response<FacilityResponse>

    @GET("review/{merchant_uuid}?api_key=${BuildConfig.API_KEY}")
    suspend fun reviewList(
        @Path("merchant_uuid") merchant_uuid:String,
        @Query("page")page:Int,
    ): Response<ReviewResponse>

    @GET("merchant/{merchant_uuid}?api_key=${BuildConfig.API_KEY}")
    suspend fun merchantDetail(
        @Path("merchant_uuid") merchant_uuid:String,
    ): Response<Merchant>


    @GET("merchant/uid/{uuid}?api_key=${BuildConfig.API_KEY}")
    suspend fun MerchantsListPaging(
        @Path("uuid") uuid:String,
        @Query("page")page:Int
    ): Response<MerchantResponse>

    @GET("merchant/uid/{user_uuid}?api_key=${BuildConfig.API_KEY}")
    suspend fun MerchantsList(
        @Path("user_uuid") uuid:String,
    ): Response<Merchant>

    @GET("merchants/recommended?api_key=${BuildConfig.API_KEY}")
    suspend fun recommendedMerchantsList(
        @Query("menu", encoded = true) menu: String,
        @Query("page")page:Int
    ): Response<MerchantResponse>


    @POST("merchant/create?api_key=${BuildConfig.API_KEY}")
    suspend fun createMerchant(
        @Body merchantRequest: MerchantRequest
    ):Response<Merchant>

    @POST("merchant/{merchant_uuid}/menu?api_key=${BuildConfig.API_KEY}")
    suspend fun createMenuMerchant(
        @Path("merchant_uuid") merchant_uuid:String,
        @Body merchantMenuRequest: MerchantMenuRequest
    ):Response<Boolean>

    @POST("menu/create/merchant?api_key=${BuildConfig.API_KEY}")
    suspend fun createMenu(
        @Body createMenuRequest: CreateMenuRequest
    ):Response<Boolean>

    @POST("facility/create/merchant?api_key=${BuildConfig.API_KEY}")
    suspend fun createFacility(
        @Body createFacilityRequest: CreateFacilityRequest
    ):Response<Boolean>

    @GET("photo_menu/merchant/{merchant_uuid}?api_key=${BuildConfig.API_KEY}")
    suspend fun photoMenuList(
        @Path("merchant_uuid") merchant_uuid: String,
    ): Response<PhotoMenuResponse>

    @POST("photo_menu/create/merchant?api_key=${BuildConfig.API_KEY}")
    suspend fun updatePhotoMenu(
        @Body updatePhotoRequest: UpdatePhotoMenuRequest
    ):Response<Boolean>

    @DELETE("merchant/photo_menu/delete/id/{id}?api_key=${BuildConfig.API_KEY}")
    suspend fun deletePhotoMenu(
        @Path("id") id:String,
    ):Response<Boolean>

    @POST("merchant/photo?api_key=${BuildConfig.API_KEY}")
    suspend fun updateMerchantPhoto(
        @Body updateMerchantRequest: UpdateMerchantRequest
    ):Response<Boolean>

    @DELETE("merchant/menu/delete/id/{id}?api_key=${BuildConfig.API_KEY}")
    suspend fun deleteMenu(
        @Path("id") id:String,
    ):Response<Boolean>

    @POST("merchant/{merchant_uuid}/status?api_key=${BuildConfig.API_KEY}")
    suspend fun updateStatusMerchant(
        @Path("merchant_uuid") merchant_uuid:String,
        @Body merchantStatusRequest: MerchantStatusRequest
    ):Response<OpenCloseModel>


    @POST("suggestion/create?api_key=${BuildConfig.API_KEY}")
    suspend fun createSuggestion(
        @Body createSuggestionRequest: CreateSuggestionRequest
    ):Response<Boolean>


}