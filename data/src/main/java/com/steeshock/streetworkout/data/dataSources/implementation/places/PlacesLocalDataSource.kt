package com.steeshock.streetworkout.data.dataSources.implementation.places

import com.steeshock.streetworkout.data.dataSources.interfaces.local.IPlacesLocalDataSource
import com.steeshock.streetworkout.data.dao.PlacesDao
import com.steeshock.streetworkout.data.repository.dto.PlaceDto
import javax.inject.Inject

class PlacesLocalDataSource @Inject constructor(
    private val placesDao: PlacesDao,
) : IPlacesLocalDataSource {

    override val allPlaces = placesDao.getPlacesFlow()
    override val allFavoritePlaces = placesDao.getFavoritePlacesFlow()

    override suspend fun getLocalFavorites(): List<String> {
        return placesDao.getFavoritePlaces().map { it.placeId }
    }

    override suspend fun getPlaceById(placeId: String): PlaceDto? {
        return placesDao.getPlaceById(placeId)
    }

    override suspend fun updatePlaceLocal(placeDto: PlaceDto) {
        placesDao.updatePlace(placeDto)
    }

    override suspend fun insertPlaceLocal(placeDto: PlaceDto) {
        placesDao.insertPlace(placeDto)
    }

    override suspend fun insertAllPlacesLocal(placesDto: List<PlaceDto>) {
        placesDao.insertAllPlaces(placesDto)
    }

    override suspend fun deletePlaceLocal(placeDto: PlaceDto) {
        placesDao.deletePlace(placeDto)
    }

    override suspend fun updatePlacesWithFavoriteList(favorites: List<String>) {
        placesDao.getAllPlaces().apply {
            forEach { it.isFavorite = favorites.contains(it.placeId) }
            placesDao.insertAllPlaces(this)
        }
    }

    override suspend fun resetFavoritesLocal() {
        placesDao.getAllPlaces().apply {
            forEach { it.isFavorite = false }
            placesDao.insertAllPlaces(this)
        }
    }

    override suspend fun clearPlacesTable() {
        placesDao.clearPlacesTable()
    }
}