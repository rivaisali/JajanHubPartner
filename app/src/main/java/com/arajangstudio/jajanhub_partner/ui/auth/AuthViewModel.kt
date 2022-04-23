package com.arajangstudio.jajanhub_partner.ui.auth

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arajangstudio.jajanhub_partner.data.JajanhubRepository
import com.arajangstudio.jajanhub_partner.data.remote.models.Result
import com.arajangstudio.jajanhub_partner.data.remote.models.User
import com.arajangstudio.jajanhub_partner.data.remote.requests.TokenRequest
import com.arajangstudio.jajanhub_partner.data.remote.requests.UserRequest
import com.arajangstudio.jajanhub_partner.utils.NetworkHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repositories: JajanhubRepository,
    private val networkHelper: NetworkHelper) :
    ViewModel() {

    val status: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    var user = MutableLiveData<User>()

    suspend fun create(full_name: String,
                       phone: String,
                       email: String,
                       password: String,
                       password_confirm: String,
                       uuid: String,
                       role: String) {
                repositories.register(
                    UserRequest(
                        full_name, phone, email, password, password_confirm, uuid, role)
                )
    }


    suspend fun updateToken(uuid: String, token: String){
    repositories.updateToken(uuid, TokenRequest(token))
    }


    fun validate(uuid: String){
        viewModelScope.launch {
            if(networkHelper.isNetworkConnected()){
                repositories.validate(uuid).let {
                 when(it){
                        is Result.Success -> {
                            status.postValue(true)
                            user.postValue(it.data)
                        }
                        is Result.Error -> status.postValue(false)
                    }
                }
            }else{
                Toast.makeText(context, "Periksa Koneksi internet anda", Toast.LENGTH_SHORT).show()
            }

        }
    }


}