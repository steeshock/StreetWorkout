package com.example.android.streetworkout.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PlacesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPlace(place: PlaceObject)

    @Query("select * from places_table")
    fun getPlaces(): MutableList<PlaceObject>

    @Query("delete from places_table")
    fun clearPlacesTable()
}