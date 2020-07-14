package com.example.android.streetworkout.data.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.android.streetworkout.data.model.PlaceObject

@Dao
interface PlacesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPlace(place: PlaceObject)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllPlaces(places: List<PlaceObject>)

    @Query("select * from places_table")
    fun getPlaces(): MutableList<PlaceObject>

    @Query("select * from places_table")
    fun getPlacesLive(): LiveData<List<PlaceObject>>

    @Query("delete from places_table")
    fun clearPlacesTable()
}