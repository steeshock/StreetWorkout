package com.example.android.streetworkout.data.api

import com.example.android.streetworkout.data.model.PlaceObject
import com.example.android.streetworkout.data.model.Post
import retrofit2.Response
import retrofit2.http.GET

interface StreetWorkoutAPI {
    @GET("steeshock/SimpleAPI/places/")
    suspend fun getPlaces(): Response<List<PlaceObject>>

    @GET("steeshock/SimpleAPI/posts/")
    suspend fun getPosts(): Response<List<Post>>
}