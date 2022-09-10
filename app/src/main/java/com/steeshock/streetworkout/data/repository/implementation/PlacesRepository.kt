package com.steeshock.streetworkout.data.repository.implementation

import com.steeshock.streetworkout.data.dataSources.local.IPlacesLocalDataSource
import com.steeshock.streetworkout.data.dataSources.remote.IPlacesRemoteDataSource
import com.steeshock.streetworkout.domain.entity.Place
import com.steeshock.streetworkout.domain.repository.IPlacesRepository
import javax.inject.Inject

class PlacesRepository @Inject constructor(
    private val placesLocalDataSource: IPlacesLocalDataSource,
    private val placesRemoteDataSource: IPlacesRemoteDataSource,
) : IPlacesRepository {

    override val allPlaces = placesLocalDataSource.allPlaces
    override val allFavoritePlaces = placesLocalDataSource.allFavoritePlaces

    override suspend fun fetchPlaces() {
        val remotePlaces = placesRemoteDataSource.getPlacesRemote()
        remotePlaces.forEach {
            val isFavorite = placesLocalDataSource.getPlaceById(it.placeId)?.isFavorite
            it.isFavorite = isFavorite == true
        }
        placesLocalDataSource.insertAllPlacesLocal(remotePlaces)
    }

    override suspend fun getLocalFavorites(): List<String> {
        return placesLocalDataSource.getLocalFavorites()
    }

    override suspend fun uploadImage(uri: String, placeId: String?): String? {
        return placesRemoteDataSource.uploadImage(uri, placeId)
    }

    override suspend fun insertPlace(newPlace: Place) {
        placesRemoteDataSource.insertPlaceRemote(newPlace)
        placesLocalDataSource.insertPlaceLocal(newPlace)
    }

    override suspend fun updatePlaceLocal(place: Place) {
        placesLocalDataSource.updatePlaceLocal(place)
    }


    override suspend fun deletePlace(place: Place): Boolean {
        return if (placesRemoteDataSource.deletePlaceRemote(place)) {
            placesLocalDataSource.deletePlaceLocal(place)
            true
        } else false
    }

    override suspend fun clearPlacesTable() {
        placesLocalDataSource.clearPlacesTable()
    }

    override suspend fun updatePlacesWithFavoriteList(favorites: List<String>) {
        placesLocalDataSource.updatePlacesWithFavoriteList(favorites)
    }

    override suspend fun resetFavoritesLocal() {
        placesLocalDataSource.resetFavoritesLocal()
    }
}