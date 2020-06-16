package com.example.android.streetworkout.data

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.example.android.streetworkout.data.database.PlacesDao
import com.example.android.streetworkout.data.database.PlacesDatabase
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

    companion object {

        @Volatile private var instance: Repository? = null

        fun getInstance(placesDao:PlacesDao) =
            instance ?: synchronized(this) {
                instance ?: Repository(placesDao).also { instance = it }
            }
    }
}