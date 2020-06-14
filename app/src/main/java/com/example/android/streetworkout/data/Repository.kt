package com.example.android.streetworkout.data

import androidx.lifecycle.LiveData
import com.example.android.streetworkout.data.database.PlacesDao
import com.example.android.streetworkout.data.model.PlaceObject

class Repository(private val placesDao: PlacesDao) {

    fun insertPlace(place: PlaceObject) {
        placesDao.insertPlace(place)
    }

    fun clearPlacesTable() {
        placesDao.clearPlacesTable()
    }

    fun getAllPlaces() : MutableList<PlaceObject> = placesDao.getPlaces()

    fun getPlacesLive(): LiveData<List<PlaceObject>> = placesDao.getPlacesLive()

    interface RepositoryOwner {
        fun obtainRepository(): Repository?
    }
}