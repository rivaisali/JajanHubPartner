package com.arajangstudio.jajanhub_partner.ui.merchant

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import com.arajangstudio.jajanhub_partner.data.JajanhubRepository
import com.arajangstudio.jajanhub_partner.data.remote.models.*
import com.arajangstudio.jajanhub_partner.data.remote.requests.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class MerchantViewModel @Inject constructor(private val repositories: JajanhubRepository) :
    ViewModel() {

    val state: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(false)
    }

    private lateinit var uuid: String

    fun setSelected(uuid: String) {
        this.uuid = uuid
    }

    suspend fun getMerchant(): Flow<Merchant> = repositories.getMerchant(uuid)


    suspend fun getDetailMerchant(): Flow<Merchant> = repositories.getDetailMerchant(uuid)


    fun getReviewMerchants(): Flow<PagingData<Review>> {
        return repositories.getListReview(uuid)
    }

    suspend fun getMenus(): Flow<ArrayList<Menus>> {
        return repositories.getMenu()
    }

    suspend fun getMenuMerchant(merchant_uuid: String): Flow<ArrayList<Menus>> {
        return repositories.getMenuMerchant(merchant_uuid)
    }

    suspend fun getFacilities(): Flow<ArrayList<FacilityAll>> {
        return repositories.getFacilities()
    }

    suspend fun createMerchant(
         user_uid:String,
         type_merchant:String,
         merchant_name:String,
         branch: Int,
         phone: String,
         instagram: String,
         location_address: String,
         location_name: String,
         location_latitude: Double,
         location_longitude: Double,
         schedule: String,
         open_time:String,
         close_time:String,
         photo: String,
    ) {
        repositories.createMerchant(MerchantRequest(user_uid, type_merchant, merchant_name, branch, phone, instagram, location_address,
            location_name, location_latitude, location_longitude, schedule, open_time,close_time, photo))

    }

    suspend fun createMenuMerchant(
        merchant_uuid: String,
        menus:String,
        facilities: String,
        photos:String,
    ) {
        repositories.createMenuMerchant(merchant_uuid, MerchantMenuRequest(menus, facilities, photos))

    }

    suspend fun createMenu(
        merchant_uuid: String,
        menu_id:String,
    ) {
        repositories.createMenuMerchant(CreateMenuRequest(merchant_uuid, menu_id))

    }

    suspend fun createFacility(
        merchant_uuid: String,
        facilities:String,
    ) {
        repositories.createFacilityMerchant(CreateFacilityRequest(merchant_uuid, facilities))

    }

    suspend fun updateMerchantPhoto(
        merchant_uuid: String,
        photo:String,
    ) {
        repositories.updateMerchantPhoto(UpdateMerchantRequest(merchant_uuid, photo))

    }


    suspend fun deleteMenu(id: String) : Boolean {
        return repositories.deleteMenu(id)
    }

    suspend fun getPhotoMenus(merchant_uuid: String): Flow<ArrayList<PhotoMenu>> {
        return repositories.getPhotoMenu(merchant_uuid)
    }

    suspend fun updateMenuPhoto(id: String, photo: String){
        repositories.updatePhotoMenu(UpdatePhotoMenuRequest(id, photo))
    }

    suspend fun createSuggestion(user_uuid: String, title: String, message: String) : Boolean{
        return  repositories.createSuggestion(CreateSuggestionRequest(user_uuid, title, message))
    }

}