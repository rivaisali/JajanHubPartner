package com.arajangstudio.jajanhub_partner.data.remote

import com.arajangstudio.jajanhub_partner.BuildConfig
import com.arajangstudio.jajanhub_partner.data.remote.models.Places
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiServiceExternal {

    @GET("/v1/revgeocode")
    suspend fun getLocation(
        @Query("at") at: String,
        @Query("apiKey") apiKey: String = BuildConfig.API_KEY_GEO
    ): Places

    companion object {
        fun create(): ApiServiceExternal {
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY

            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL_GEO)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
            return retrofit.create(ApiServiceExternal::class.java)
        }
    }
}