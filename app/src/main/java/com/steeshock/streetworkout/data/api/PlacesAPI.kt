package com.steeshock.streetworkout.data.api

import com.steeshock.streetworkout.data.model.Category
import com.steeshock.streetworkout.data.model.Place
import retrofit2.Response
import retrofit2.http.GET

interface PlacesAPI {
    /**
     * Obsolete RxJava approach
     */
    //@GET("steeshock/SimpleAPI/places/")
    //fun getPlaces(): Observable<List<Place>>

    /**
     * Obsolete RxJava approach
     */
    //@GET("steeshock/SimpleAPI/categories/")
    //fun getCategories(): Observable<List<Category>>

    @GET("steeshock/SimpleAPI/places/")
    suspend fun getPlaces(): Response<List<Place>>

    @GET("steeshock/SimpleAPI/categories/")
    suspend fun getCategories(): Response<List<Category>>
}