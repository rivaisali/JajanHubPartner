package com.arajangstudio.jajanhub_partner.data.remote

import com.arajangstudio.jajanhub_partner.data.remote.models.*
import com.arajangstudio.jajanhub_partner.data.remote.requests.*
import com.arajangstudio.jajanhub_partner.data.remote.responses.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class JajanHubService @Inject constructor(private val apiServices: ApiServices) : BaseService() {

    suspend fun register(userRequest: UserRequest) : Result<User> {
        return createCall { apiServices.register(userRequest) }
    }

    suspend fun validate(uuid: String) :Result<User> {
        return createCall { apiServices.validate(uuid) }
    }

    suspend fun updateLastLocation(uuid: String, locationRequest: LocationRequest): Result<User>{
        return createCall { apiServices.updateLastLocation(uuid, locationRequest) }
    }

    suspend fun updateToken(uuid: String, tokenRequest: TokenRequest): Result<User>{
        return createCall { apiServices.updateToken(uuid, tokenRequest) }
    }

    suspend fun createMerchant(merchantRequest: MerchantRequest) : Result<Merchant> {
        return createCall { apiServices.createMerchant(merchantRequest) }
    }

    suspend fun createMenuMerchant(merchant_uuid:String, menuMerchantRequest: MerchantMenuRequest) : Result<Boolean> {
        return createCall { apiServices.createMenuMerchant(merchant_uuid,menuMerchantRequest) }
    }

    suspend fun createMenu(createMenuRequest: CreateMenuRequest) : Result<Boolean> {
        return createCall { apiServices.createMenu(createMenuRequest) }
    }

    suspend fun createFacility(createFacilityRequest: CreateFacilityRequest) : Result<Boolean> {
        return createCall { apiServices.createFacility(createFacilityRequest) }
    }

    suspend fun fetchMenuPhoto(merchant_uuid: String) : Result<PhotoMenuResponse> {
        return createCall { apiServices.photoMenuList(merchant_uuid) }
    }

    suspend fun updateMenuPhoto(updatePhotoRequest: UpdatePhotoMenuRequest) : Result<Boolean> {
        return createCall { apiServices.updatePhotoMenu(updatePhotoRequest) }
    }

    suspend fun deletePhotoMenu(id: String) : Result<Boolean> {
        return createCall { apiServices.deletePhotoMenu(id) }
    }

    suspend fun updateMerchantPhoto(updateMerchantRequest: UpdateMerchantRequest) : Result<Boolean> {
        return createCall { apiServices.updateMerchantPhoto(updateMerchantRequest) }
    }

    suspend fun deleteMenu(id: String) : Result<Boolean> {
        return createCall { apiServices.deleteMenu(id) }
    }

    suspend fun updateStatusMerchant(merchant_uuid:String, merchantStatusRequest: MerchantStatusRequest) : Result<OpenCloseModel> {
        return createCall { apiServices.updateStatusMerchant(merchant_uuid,merchantStatusRequest) }
    }


    suspend fun fetchMerchantPaging(uuid :String, page: Int) : Result<MerchantResponse> {
        return createCall { apiServices.MerchantsListPaging(uuid, page) }
    }

    suspend fun fetchMerchant(user_uid: String) : Result<Merchant> {
        return createCall { apiServices.MerchantsList(user_uid) }
    }

    suspend fun fetchListMenus() : Result<MenuResponse> {
        return createCall { apiServices.menuList() }
    }

    suspend fun fetchListMenuMerchant(merchant_uuid: String) : Result<MenuResponse> {
        return createCall { apiServices.menuListMerchant(merchant_uuid) }
    }


    suspend fun fetchListDetailMerchant(merchant_id: String) : Result<Merchant> {
        return createCall { apiServices.merchantDetail(merchant_id) }
    }

    suspend fun fetchListFacilities() : Result<FacilityResponse> {
        return createCall { apiServices.facilitiesList() }
    }
    suspend fun fetchListReview(merchant_id: String, page: Int) : Result<ReviewResponse> {
        return createCall { apiServices.reviewList(merchant_id, page) }
    }

    suspend fun createSuggestion(createSuggestionRequest: CreateSuggestionRequest) : Result<Boolean> {
        return createCall { apiServices.createSuggestion(createSuggestionRequest) }
    }


}