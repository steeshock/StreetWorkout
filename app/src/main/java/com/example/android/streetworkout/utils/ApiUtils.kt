package com.example.android.streetworkout.utils

import com.example.android.streetworkout.BuildConfig
import com.example.android.streetworkout.data.api.PlacesAPI
import com.google.gson.Gson
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class ApiUtils {

    companion object {
        private var client: OkHttpClient? = null
        private var retrofit: Retrofit? = null
        private var gson: Gson? = null
        private var api: PlacesAPI? = null

        @Volatile
        private var instance: PlacesAPI? = null

        fun getInstance(): PlacesAPI {
            return instance ?: synchronized(this) {
                instance ?: getApiService().also { instance = it }
            }!!
        }

        private fun getClient(): OkHttpClient? {
            if (client == null) {
                val builder = OkHttpClient().newBuilder()
                client = builder.build()
            }
            return client
        }

        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
        private fun getRetrofit(): Retrofit? {
            if (gson == null) {
                gson = Gson()
            }
            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                    .baseUrl(BuildConfig.API_URL)
                    .client(getClient())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build()
            }
            return retrofit
        }

        private fun getApiService(): PlacesAPI? {
            if (api == null) {
                api = getRetrofit()?.create(PlacesAPI::class.java)
            }
            return api
        }
    }
}