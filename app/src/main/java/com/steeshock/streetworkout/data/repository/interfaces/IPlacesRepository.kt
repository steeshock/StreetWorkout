package com.steeshock.streetworkout.data.repository.interfaces

import android.net.Uri
import androidx.lifecycle.LiveData
import com.steeshock.streetworkout.data.model.Place

interface IPlacesRepository {

    val allPlaces: LiveData<List<Place>>
    val allFavoritePlaces: LiveData<List<Place>>

    /**
     * Fetch places from remote source and save data to local storage
     */
    suspend fun fetchPlaces(): Boolean

    /**
     * Upload image with file [uri] to Firebase Storage
     * @return Uri with uploaded file
     */
    suspend fun uploadImage(uri: Uri, placeId: String?): Uri?

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

    /**
     * Update places favorite state with favorites from User
     *
     * Used "additive" merging - if not authorised user add to favorites,
     * we save this data and just merge with fetched user data favorites
     */
    suspend fun updatePlacesWithFavoriteList(favorites: List<String>)

    /**
     * Reset all favorite places, commonly after logout
     */
    suspend fun resetFavorites()
}