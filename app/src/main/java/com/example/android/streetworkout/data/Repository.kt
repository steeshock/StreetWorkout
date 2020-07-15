package com.example.android.streetworkout.data

import androidx.lifecycle.LiveData
import com.example.android.streetworkout.data.api.PlacesAPI
import com.example.android.streetworkout.data.database.PlacesDao
import com.example.android.streetworkout.data.model.PlaceObject

class Repository(
    private val placesDao: PlacesDao,
    private val placesAPI: PlacesAPI
) {

    val allPlaces: LiveData<List<PlaceObject>> = placesDao.getPlacesLive()

    fun insertPlace(place: PlaceObject) {
        placesDao.insertPlace(place)
    }

    suspend fun updatePlaces() {
        val response = placesAPI.getPlaces().body()
        response?.let {
            placesDao.removeAllPlacesExceptFavorites(false)
            placesDao.insertAllPlaces(it)
        }
    }

    fun clearPlacesTable() {
        placesDao.clearPlacesTable()
    }

    fun removeAllPlacesExceptFavorites(boolean: Boolean) {
        placesDao.removeAllPlacesExceptFavorites(boolean)
    }

    companion object {

        @Volatile
        private var instance: Repository? = null

        fun getInstance(placesDao: PlacesDao, placesAPI: PlacesAPI) =
            instance ?: synchronized(this) {
                instance ?: Repository(placesDao, placesAPI).also { instance = it }
            }
    }
}