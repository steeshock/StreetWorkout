package com.example.android.streetworkout.model

class Storage(placesDao: PlacesDao) {

    private val mPlacesDao: PlacesDao = placesDao

    fun insertPlace(place: PlaceObject) {
        mPlacesDao.insertPlace(place)
    }

    fun clearPlacesTable() {
        mPlacesDao.clearPlacesTable()
    }

    fun getAllPlaces() : MutableList<PlaceObject> = mPlacesDao.getPlaces()

    interface StorageOwner {
        fun obtainStorage(): Storage?
    }

    companion object {
        const val PAGE_SIZE = 10
    }
}