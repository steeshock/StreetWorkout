package com.steeshock.streetworkout.data.api

import com.steeshock.streetworkout.data.model.CategoryDto
import com.steeshock.streetworkout.data.model.PlaceDto
import retrofit2.Response
import retrofit2.http.GET

interface PlacesAPI {

    @GET("steeshock/SimpleAPI/places/")
    suspend fun getPlaces(): Response<List<PlaceDto>>

    @GET("steeshock/SimpleAPI/categories/")
    suspend fun getCategories(): Response<List<CategoryDto>>
}