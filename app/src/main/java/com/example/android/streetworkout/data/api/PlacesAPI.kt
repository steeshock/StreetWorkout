package com.example.android.streetworkout.data.api

import com.example.android.streetworkout.data.model.PlaceObject
import retrofit2.Response
import retrofit2.http.GET

interface PlacesAPI {
    @GET("steeshock/SimpleAPI/places/")
    suspend fun getPlaces(): Response<List<PlaceObject>>
}