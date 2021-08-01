package com.steeshock.android.streetworkout.data.api

import com.steeshock.android.streetworkout.data.model.Category
import com.steeshock.android.streetworkout.data.model.Place
import io.reactivex.rxjava3.core.Observable
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface PlacesAPI {
    @GET("steeshock/SimpleAPI/places/")
    fun getPlaces(): Observable<List<Place>>

    @GET("steeshock/SimpleAPI/categories/")
    fun getCategories(): Observable<List<Category>>
}