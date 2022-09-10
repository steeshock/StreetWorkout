package com.steeshock.streetworkout.data.dataSources.implementation

import com.steeshock.streetworkout.data.dataSources.local.IPlacesLocalDataSource
import com.steeshock.streetworkout.data.database.PlacesDao
import com.steeshock.streetworkout.data.mappers.mapToDto
import com.steeshock.streetworkout.data.mappers.mapToEntity
import com.steeshock.streetworkout.data.repository.dto.PlaceDto
import com.steeshock.streetworkout.domain.entity.Place
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PlacesLocalDataSource @Inject constructor(
    private val placesDao: PlacesDao,
) : IPlacesLocalDataSource {

    override val allPlaces = placesDao.getPlacesFlow().map { places ->
        places.map { it.mapToEntity() }
    }
    override val allFavoritePlaces = placesDao.getFavoritePlacesFlow().map { places ->
        places.map { it.mapToEntity() }
    }

    override suspend fun getLocalFavorites(): List<String> {
        return placesDao.getFavoritePlaces().map { it.placeId }
    }

    override suspend fun getPlaceById(placeId: String): PlaceDto? {
        return placesDao.getPlaceById(placeId)
    }

    override suspend fun updatePlaceLocal(place: Place) {
        placesDao.updatePlace(place.mapToDto())
    }

    override suspend fun insertPlaceLocal(newPlace: Place) {
        placesDao.insertPlace(newPlace.mapToDto())
    }

    override suspend fun insertAllPlacesLocal(places: List<PlaceDto>) {
        placesDao.insertAllPlaces(places)
    }

    override suspend fun deletePlaceLocal(place: Place) {
        placesDao.deletePlace(place.mapToDto())
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