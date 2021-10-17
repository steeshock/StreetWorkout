package com.steeshock.android.streetworkout.data.repository.interfaces

import android.net.Uri
import androidx.lifecycle.LiveData
import com.steeshock.android.streetworkout.data.api.APIResponse
import com.steeshock.android.streetworkout.data.model.Place

interface IPlacesRepository {

    val allPlaces: LiveData<List<Place>>
    val allFavoritePlaces: LiveData<List<Place>>

    /**
     * Fetch places from remote source and return data to [onResponse] callback
     */
    suspend fun fetchPlaces(onResponse: APIResponse<List<Place>>)

    /**
     * Upload image with file [uri] to Firebase Storage
     * @return Uri with uploaded file
     */
    suspend fun uploadImage(uri: Uri, placeUUID: String): Uri?

    /**
     * Insert new place in local Places table
     */
    suspend fun insertPlaceLocal(newPlace: Place)

    /**
     * Insert new place in Remote Places storage
     */
    suspend fun insertPlaceRemote(newPlace: Place)

    /**
     * Insert list of places in Places table
     */
    suspend fun insertAllPlaces(places: List<Place>)

    /**
     * Update place in Places table
     */
    suspend fun updatePlace(place: Place)

    /**
     * Remove all data from Places table
     */
    suspend fun clearPlacesTable()
}