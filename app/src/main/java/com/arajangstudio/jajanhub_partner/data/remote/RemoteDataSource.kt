package com.arajangstudio.jajanhub_partner.data.remote

import com.arajangstudio.jajanhub_partner.data.remote.models.*
import com.arajangstudio.jajanhub_partner.data.remote.requests.*
import com.arajangstudio.jajanhub_partner.data.remote.responses.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteDataSource @Inject constructor(val jajanHubService: JajanHubService) {

    suspend fun register(userRequest: UserRequest) : User {
        return when(val result = jajanHubService.register(userRequest)){
            is Result.Success -> result.data
            is Result.Error -> throw result.error
        }
    }

    suspend fun validate(uuid: String) : Result<User> {
      return jajanHubService.validate(uuid)
    }

    suspend fun createMerchant(merchantRequest: MerchantRequest) : Merchant {
        return when(val result = jajanHubService.createMerchant(merchantRequest)){
            is Result.Success -> result.data
            is Result.Error -> throw result.error
        }
    }

    suspend fun createMenuMerchant(merchant_uuid:String, menuMerchantRequest: MerchantMenuRequest) : Boolean {
        return when(val result = jajanHubService.createMenuMerchant(merchant_uuid, menuMerchantRequest)){
            is Result.Success -> result.data
            is Result.Error -> throw result.error
        }
    }

    suspend fun createMenu(createMenuRequest: CreateMenuRequest) : Boolean {
        return when(val result = jajanHubService.createMenu(createMenuRequest)){
            is Result.Success -> result.data
            is Result.Error -> throw result.error
        }
    }

    suspend fun createFacility(createFacilityRequest: CreateFacilityRequest) : Boolean {
        return when(val result = jajanHubService.createFacility(createFacilityRequest)){
            is Result.Success -> result.data
            is Result.Error -> throw result.error
        }
    }

    suspend fun updateMenuPhoto(updatePhotoRequest: UpdatePhotoMenuRequest) : Boolean {
        return when(val result = jajanHubService.updateMenuPhoto(updatePhotoRequest)){
            is Result.Success -> result.data
            is Result.Error -> throw result.error
        }
    }

    suspend fun deletePhotoMenu(id:String) : Boolean {
        return when(val result = jajanHubService.deletePhotoMenu(id)){
            is Result.Success -> result.data
            is Result.Error -> throw result.error
        }
    }

    suspend fun getPhotoMenu(merchant_uuid: String) : PhotoMenuResponse {
        return when(val result = jajanHubService.fetchMenuPhoto(merchant_uuid)){
            is Result.Success -> result.data
            is Result.Error -> throw result.error
        }
    }

    suspend fun updateMerchantPhoto(updateMerchantRequest: UpdateMerchantRequest) : Boolean {
        return when(val result = jajanHubService.updateMerchantPhoto(updateMerchantRequest)){
            is Result.Success -> result.data
            is Result.Error -> throw result.error
        }
    }

    suspend fun deleteMenu(id:String) : Boolean {
        return when(val result = jajanHubService.deleteMenu(id)){
            is Result.Success -> result.data
            is Result.Error -> throw result.error
        }
    }

    suspend fun updateStatusMerchant(merchant_uuid:String,  merchantStatusRequest: MerchantStatusRequest) : OpenCloseModel {
        return when(val result = jajanHubService.updateStatusMerchant(merchant_uuid, merchantStatusRequest)){
            is Result.Success -> result.data
            is Result.Error -> throw result.error
        }
    }


    suspend fun updatetoken(uuid: String, tokenRequest: TokenRequest) : Result<User> {
        return jajanHubService.updateToken(uuid, tokenRequest)
    }

    suspend fun getListMerchant(uuid: String, page: Int) : MerchantResponse {
        return when(val result = jajanHubService.fetchMerchantPaging(uuid, page)){
            is Result.Success -> result.data
            is Result.Error -> throw result.error
        }
    }

    suspend fun getMerchant(user_uid: String) : Merchant {
        return when(val result = jajanHubService.fetchMerchant(user_uid)){
            is Result.Success -> result.data
            is Result.Error -> throw result.error
        }
    }

    suspend fun getListMenus() : MenuResponse {
        return when(val result = jajanHubService.fetchListMenus()){
            is Result.Success -> result.data
            is Result.Error -> throw result.error
        }
    }

    suspend fun getListMenuMerchant(merchant_uuid: String) : MenuResponse {
        return when(val result = jajanHubService.fetchListMenuMerchant(merchant_uuid)){
            is Result.Success -> result.data
            is Result.Error -> throw result.error
        }
    }

    suspend fun getListFacilities() : FacilityResponse {
        return when(val result = jajanHubService.fetchListFacilities()){
            is Result.Success -> result.data
            is Result.Error -> throw result.error
        }
    }

    suspend fun getDetailMerchant(merchant_uuid: String) : Merchant {
        return when(val result = jajanHubService.fetchListDetailMerchant(merchant_uuid)){
            is Result.Success -> result.data
            is Result.Error -> throw result.error
        }
    }


    suspend fun getListReviews(merchant_uuid: String, page: Int) : ReviewResponse {
        return when(val result = jajanHubService.fetchListReview(merchant_uuid, page)){
            is Result.Success -> result.data
            is Result.Error -> throw result.error
        }
    }

    suspend fun createSuggestion(createSuggestionRequest: CreateSuggestionRequest) : Boolean {
        return when(val result = jajanHubService.createSuggestion(createSuggestionRequest)){
            is Result.Success -> result.data
            is Result.Error -> throw result.error
        }
    }


}