package com.arajangstudio.jajanhub_partner.ui.home

import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import com.arajangstudio.jajanhub_partner.data.JajanhubRepository
import com.arajangstudio.jajanhub_partner.data.remote.models.Merchant
import com.arajangstudio.jajanhub_partner.data.remote.models.OpenCloseModel
import com.arajangstudio.jajanhub_partner.data.remote.requests.MerchantStatusRequest
import com.arajangstudio.jajanhub_partner.data.remote.requests.TokenRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val repositories: JajanhubRepository) :
    ViewModel() {



    suspend fun updateToken(uuid: String, token:String){
        repositories.updateToken(uuid, TokenRequest(token))
    }

    private lateinit var uuid: String

    fun setSelected(uuid: String) {
        this.uuid = uuid
    }

    suspend fun getMerchant(): Flow<Merchant> = repositories.getMerchant(uuid)

    fun getMerchants(uuid: String): Flow<PagingData<Merchant>> {
        return repositories.getListMerchant(uuid)
    }

    suspend fun updateStatuserchant(
        merchant_uuid: String,
        status:String
    ):Flow<OpenCloseModel> {
        return repositories.updateStatusMerchant(merchant_uuid, MerchantStatusRequest(status))

    }

}