package com.steeshock.streetworkout.data.dataSources.interfaces.local

import com.steeshock.streetworkout.data.repository.dto.PlaceDto
import com.steeshock.streetworkout.domain.entity.Place
import kotlinx.coroutines.flow.Flow

interface IPlacesLocalDataSource {

    val allPlaces: Flow<List<Place>>
    val allFavoritePlaces: Flow<List<Place>>

    /**
     * Get favorite places id's in common List
     */
    suspend fun getLocalFavorites(): List<String>

    /**
     * Returns place by id from local storage
     */
    suspend fun getPlaceById(placeId: String): PlaceDto?

    /**
     * Update place in Places table
     */
    suspend fun updatePlaceLocal(place: Place)

    /**
     * Insert new place in Local Places storage
     */
    suspend fun insertPlaceLocal(newPlace: Place)

    /**
     * Insert all places in Local Places storage
     */
    suspend fun insertAllPlacesLocal(places: List<PlaceDto>)

    /**
     * Delete place in local storage
     */
    suspend fun deletePlaceLocal(place: Place)

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