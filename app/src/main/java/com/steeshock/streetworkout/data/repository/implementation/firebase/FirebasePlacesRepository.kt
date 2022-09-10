package com.steeshock.streetworkout.data.repository.implementation.firebase

import com.steeshock.streetworkout.data.dataSources.remote.IPlacesRemoteDataSource
import com.steeshock.streetworkout.data.database.PlacesDao
import com.steeshock.streetworkout.data.mappers.mapToDto
import com.steeshock.streetworkout.data.mappers.mapToEntity
import com.steeshock.streetworkout.interactor.entity.Place
import com.steeshock.streetworkout.interactor.repository.IPlacesRepository
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Repository for work with Firebase Realtime Database
 */
open class FirebasePlacesRepository @Inject constructor(
    private val placesDao: PlacesDao,
    private val placesRemoteDataSource: IPlacesRemoteDataSource,
) : IPlacesRepository {

    override val allPlaces = placesDao.getPlacesFlow().map { places ->
        places.map { it.mapToEntity() }
    }
    override val allFavoritePlaces = placesDao.getFavoritePlacesFlow().map { places ->
        places.map { it.mapToEntity() }
    }

    override suspend fun fetchPlaces() {
        val remotePlaces = placesRemoteDataSource.getPlacesRemote()
        remotePlaces.forEach {
            val isFavorite = placesDao.getPlaceById(it.placeId)?.isFavorite
            it.isFavorite = isFavorite == true
        }
        placesDao.insertAllPlaces(remotePlaces)
    }

    override suspend fun getLocalFavorites(): List<String> {
        return placesDao.getFavoritePlaces().map { it.placeId }
    }

    override suspend fun uploadImage(uri: String, placeId: String?): String? {
        return placesRemoteDataSource.uploadImage(uri, placeId)
    }

    override suspend fun insertPlace(newPlace: Place) {
        placesRemoteDataSource.insertPlaceRemote(newPlace)
        placesDao.insertPlace(newPlace.mapToDto())
    }

    override suspend fun updatePlaceLocal(place: Place) {
        placesDao.updatePlace(place.mapToDto())
    }


    override suspend fun deletePlace(place: Place): Boolean {
        return if (placesRemoteDataSource.deletePlaceRemote(place)) {
            placesDao.deletePlace(place.mapToDto())
            true
        } else false
    }

    override suspend fun clearPlacesTable() {
        placesDao.clearPlacesTable()
    }

    override suspend fun updatePlacesWithFavoriteList(favorites: List<String>) {
        placesDao.getAllPlaces().apply {
            forEach { it.isFavorite = favorites.contains(it.placeId) }
            placesDao.insertAllPlaces(this)
        }
    }

    override suspend fun resetFavorites() {
        placesDao.getAllPlaces().apply {
            forEach { it.isFavorite = false }
            placesDao.insertAllPlaces(this)
        }
    }
}