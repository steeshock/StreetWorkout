package com.steeshock.streetworkout.interactor.repository

import com.steeshock.streetworkout.interactor.entity.Place
import kotlinx.coroutines.flow.Flow

interface IPlacesRepository {

    val allPlaces: Flow<List<Place>>
    val allFavoritePlaces: Flow<List<Place>>

    /**
     * Fetch places from remote source and save data to local storage
     */
    suspend fun fetchPlaces(): Boolean

    /**
     * Get favorite places id's in common List
     */
    suspend fun getLocalFavorites(): List<String>

    /**
     * Upload image with file [uri] to Firebase Storage
     * @return Uri with uploaded file
     */
    suspend fun uploadImage(uri: String, placeId: String?): String?

    /**
     * Insert new place in local Places table
     */
    suspend fun insertPlaceLocal(newPlace: Place)

    /**
     * Insert new place in Remote Places storage
     */
    suspend fun insertPlaceRemote(newPlace: Place)

    /**
     * Update place in Places table
     */
    suspend fun updatePlace(place: Place)

    /**
     * Delete place in local and remote storage
     */
    suspend fun deletePlace(place: Place): Boolean

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