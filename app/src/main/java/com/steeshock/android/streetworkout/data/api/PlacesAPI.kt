package com.steeshock.android.streetworkout.data.api

import com.steeshock.android.streetworkout.data.model.Place
import retrofit2.Response
import retrofit2.http.GET

interface PlacesAPI {
    @GET("steeshock/SimpleAPI/places/")
    suspend fun getPlaces(): Response<List<Place>>
}