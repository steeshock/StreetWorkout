package com.steeshock.streetworkout.data.api

import com.steeshock.streetworkout.BuildConfig
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiUtils {

    companion object {
        private lateinit var client: OkHttpClient
        private lateinit var retrofit: Retrofit
        private lateinit var api: PlacesAPI

        @Volatile
        private var instance: PlacesAPI? = null

        fun getInstance(): PlacesAPI {
            return instance ?: synchronized(this) {
                instance ?: getApiService().also { instance = it }
            }!!
        }

        private fun getClient(): OkHttpClient {
            val builder = OkHttpClient().newBuilder()
            client = builder.build()
            return client
        }

        private fun getRetrofit(): Retrofit {
            retrofit = Retrofit.Builder()
                .baseUrl(BuildConfig.API_URL)
                .client(getClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit
        }

        private fun getApiService(): PlacesAPI? {
            return getRetrofit().create(PlacesAPI::class.java)
        }
    }
}