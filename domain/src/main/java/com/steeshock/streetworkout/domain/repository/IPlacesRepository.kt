package com.steeshock.streetworkout.domain.repository

import com.steeshock.streetworkout.domain.entity.Place
import kotlinx.coroutines.flow.Flow

interface IPlacesRepository {

    val allPlaces: Flow<List<Place>>
    val allFavoritePlaces: Flow<List<Place>>

    /**
     * Fetch places from remote source and save data to local storage
     */
    suspend fun fetchPlaces()

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
     * Insert new place locally and remote
     */
    suspend fun insertPlace(newPlace: Place)

    /**
     * Update place in Places table
     */
    suspend fun updatePlaceLocal(place: Place)

    /**
     * Delete place in local and remote storage
     */
    suspend fun deletePlace(place: Place): Boolean

    /**
     * Update places favorite state with favorites from User
     *
     * Used "additive" merging - if not authorised user add to favorites,
     * we save this data and just merge with fetched user data favorites
     */
    suspend fun updatePlacesWithFavoriteList(favorites: List<String>)

    /**
     * Reset all favorite places locally, commonly after logout
     */
    suspend fun resetFavoritesLocal()

    /**
     * Remove all data from Places table
     * TODO Temporary function for easy testing
     */
    suspend fun clearPlacesTable()
}