package com.arajangstudio.jajanhub_partner.data.remote

import com.arajangstudio.jajanhub_partner.data.remote.exceptions.*
import com.arajangstudio.jajanhub_partner.data.remote.models.Result
import retrofit2.HttpException
import retrofit2.Response
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.UnknownHostException

abstract class BaseService {

    protected suspend fun<T: Any> createCall(call: suspend () -> Response<T>) : Result<T> {
        val response: Response<T>
        try {
            response = call.invoke()
        }catch (t: Throwable){
            t.printStackTrace()
            return Result.Error(mapToNetworkError(t))
        }

        if (response.isSuccessful){
            if (response.body() != null){
                return Result.Success(response.body()!!)
            }
        }
        else{
            val errorBody = response.errorBody()
            return if (errorBody != null){
                Result.Error(mapApiException(response.code(), errorBody.toString()))
            } else {
                Result.Error(mapApiException(0, ""))
            }
        }
        return Result.Error(HttpException(response))
    }

    private fun mapApiException(code: Int, message: String): Exception {
        return when(code){
            HttpURLConnection.HTTP_NOT_FOUND -> NotFoundException()
            HttpURLConnection.HTTP_UNAUTHORIZED -> UnAuthorizedException()
            else -> OtherException(message)
        }
    }

    private fun mapToNetworkError(t: Throwable): Exception {
        return when(t){
            is SocketTimeoutException
            -> SocketTimeoutException("Connection Timed Out")
            is UnknownHostException
            -> NoInternetException()
            else
            -> UnKnownException()

        }
    }

}