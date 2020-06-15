package com.example.android.streetworkout.data

import androidx.lifecycle.LiveData
import com.example.android.streetworkout.data.database.PlacesDao
import com.example.android.streetworkout.data.model.PlaceObject

class Repository(private val placesDao: PlacesDao) {

    val allPlaces: LiveData<List<PlaceObject>> = placesDao.getPlacesLive()

    fun insertPlace(place: PlaceObject) {
        placesDao.insertPlace(place)
    }

    fun getAllPlaces() : MutableList<PlaceObject> = placesDao.getPlaces()

    fun clearPlacesTable() {
        placesDao.clearPlacesTable()
    }
}