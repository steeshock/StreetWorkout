package com.steeshock.streetworkout.data.dataSources.interfaces.local

import com.steeshock.streetworkout.data.repository.dto.PlaceDto
import kotlinx.coroutines.flow.Flow

interface IPlacesLocalDataSource {

    val allPlaces: Flow<List<PlaceDto>>
    val allFavoritePlaces: Flow<List<PlaceDto>>

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
    suspend fun updatePlaceLocal(placeDto: PlaceDto)

    /**
     * Insert new place in Local Places storage
     */
    suspend fun insertPlaceLocal(placeDto: PlaceDto)

    /**
     * Insert all places in Local Places storage
     */
    suspend fun insertAllPlacesLocal(placesDto: List<PlaceDto>)

    /**
     * Delete place in local storage
     */
    suspend fun deletePlaceLocal(placeDto: PlaceDto)

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