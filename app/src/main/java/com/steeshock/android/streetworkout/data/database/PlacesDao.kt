package com.steeshock.android.streetworkout.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.steeshock.android.streetworkout.data.model.Category
import com.steeshock.android.streetworkout.data.model.Place


@Dao
interface PlacesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPlace(place: Place)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllPlaces(places: List<Place>)

    @Query("SELECT * FROM ${Place.TABLE_NAME}")
    fun getPlacesLive(): LiveData<List<Place>>

    @Query("SELECT * FROM ${Place.TABLE_NAME} WHERE isFavorite")
    fun getFavoritePlacesLive(): LiveData<List<Place>>

    @Query("DELETE FROM ${Place.TABLE_NAME}")
    fun clearPlacesTable()

    @Update
    fun updatePlace(place: Place)
}