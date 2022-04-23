package com.arajangstudio.jajanhub_partner.data


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.arajangstudio.jajanhub_partner.data.paging.MerchantDataSource
import com.arajangstudio.jajanhub_partner.data.paging.ReviewDataSource
import com.arajangstudio.jajanhub_partner.data.remote.RemoteDataSource
import com.arajangstudio.jajanhub_partner.data.remote.models.*
import com.arajangstudio.jajanhub_partner.data.remote.requests.*
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class JajanhubRepository @Inject constructor(private val remoteDataSource: RemoteDataSource) {


    suspend fun register(userRequest: UserRequest) : User {
        return remoteDataSource.register(userRequest)
    }

    suspend fun validate(uuid : String) : Result<User> {
        return remoteDataSource.validate(uuid)
    }

    suspend fun updateToken(uuid: String, tokenRequest: TokenRequest): Result<User>{
        return remoteDataSource.updatetoken(uuid, tokenRequest)
    }

    suspend fun createMerchant(merchantRequest: MerchantRequest) : Merchant {
        return remoteDataSource.createMerchant(merchantRequest)
    }

    suspend fun createMenuMerchant(merchant_uuid:String, menuMerchantRequest: MerchantMenuRequest) : Boolean {
        return remoteDataSource.createMenuMerchant(merchant_uuid, menuMerchantRequest)
    }

    suspend fun createMenuMerchant(createMenuRequest: CreateMenuRequest) : Boolean {
        return remoteDataSource.createMenu(createMenuRequest)
    }

    suspend fun createFacilityMerchant(createFacilityRequest: CreateFacilityRequest) : Boolean {
        return remoteDataSource.createFacility(createFacilityRequest)
    }

    suspend fun updateMerchantPhoto(updateMerchantRequest: UpdateMerchantRequest) : Boolean {
        return remoteDataSource.updateMerchantPhoto(updateMerchantRequest)
    }

    suspend fun deleteMenu(id: String) : Boolean {
        return remoteDataSource.deleteMenu(id)
    }

    suspend fun getPhotoMenu(merchant_uuid: String) : Flow<ArrayList<PhotoMenu>> {
        val menu = MutableLiveData<ArrayList<PhotoMenu>>()
        val item = remoteDataSource.getPhotoMenu(merchant_uuid)
        menu.postValue(item.results)
        return menu.asFlow()
    }

    suspend fun updatePhotoMenu(updatePhotoMenuRequest: UpdatePhotoMenuRequest) : Boolean {
        return remoteDataSource.updateMenuPhoto(updatePhotoMenuRequest)
    }

    suspend fun deletePhotoMenu(id: String) : Boolean {
        return remoteDataSource.deletePhotoMenu(id)
    }

    suspend fun updateStatusMerchant(merchant_uuid:String, merchantStatusRequest: MerchantStatusRequest)
    : Flow<OpenCloseModel> {
        val detail = MutableLiveData<OpenCloseModel>()
        val item = remoteDataSource.updateStatusMerchant(merchant_uuid, merchantStatusRequest)
        detail.postValue(item)
        return detail.asFlow()
    }


    fun getListMerchant(uuid: String): Flow<PagingData<Merchant>> {
        return Pager(
            config = PagingConfig(
                pageSize = NETWORK_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                MerchantDataSource(remoteDataSource, uuid)
            }
        ).flow

    }

    suspend fun getMerchant(user_uid : String) : Flow<Merchant> {
        val detail = MutableLiveData<Merchant>()
        val item = remoteDataSource.getMerchant(user_uid)
        detail.postValue(item)
        return detail.asFlow()
    }

    suspend fun getDetailMerchant(merchant_id : String) : Flow<Merchant> {
        val detail = MutableLiveData<Merchant>()
        val item = remoteDataSource.getDetailMerchant(merchant_id)
        detail.postValue(item)
        return detail.asFlow()
    }

    fun getListReview(merchant_id: String): Flow<PagingData<Review>> {
        return Pager(
            config = PagingConfig(
                pageSize = NETWORK_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                ReviewDataSource(remoteDataSource, merchant_id)
            }
        ).flow

    }

    suspend fun getMenu() : Flow<ArrayList<Menus>> {
        val menu = MutableLiveData<ArrayList<Menus>>()
        val item = remoteDataSource.getListMenus()
        menu.postValue(item.results)
        return menu.asFlow()
    }

    suspend fun getMenuMerchant(merchant_id: String) : Flow<ArrayList<Menus>> {
        val menu = MutableLiveData<ArrayList<Menus>>()
        val item = remoteDataSource.getListMenuMerchant(merchant_id)
        menu.postValue(item.results)
        return menu.asFlow()
    }

    suspend fun getFacilities() : Flow<ArrayList<FacilityAll>> {
        val menu = MutableLiveData<ArrayList<FacilityAll>>()
        val item = remoteDataSource.getListFacilities()
        menu.postValue(item.results)
        return menu.asFlow()
    }


    suspend fun createSuggestion(createSuggestionRequest: CreateSuggestionRequest) : Boolean {
        return remoteDataSource.createSuggestion(createSuggestionRequest)
    }

    companion object {
        private const val NETWORK_PAGE_SIZE = 8
    }
}