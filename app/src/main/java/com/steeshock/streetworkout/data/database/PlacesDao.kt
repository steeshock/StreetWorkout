package com.steeshock.streetworkout.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.steeshock.streetworkout.data.model.PlaceDto


@Dao
interface PlacesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPlace(placeDto: PlaceDto)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllPlaces(placeDtos: List<PlaceDto>)

    @Query("SELECT * FROM ${PlaceDto.TABLE_NAME}")
    fun getPlacesLive(): LiveData<List<PlaceDto>>

    @Query("SELECT * FROM ${PlaceDto.TABLE_NAME} WHERE isFavorite")
    fun getFavoritePlacesLive(): LiveData<List<PlaceDto>>

    @Query("DELETE FROM ${PlaceDto.TABLE_NAME}")
    fun clearPlacesTable()

    @Query("SELECT * FROM ${PlaceDto.TABLE_NAME}")
    fun getAllPlaces(): List<PlaceDto>

    @Query("SELECT * FROM ${PlaceDto.TABLE_NAME} WHERE isFavorite")
    fun getFavoritePlaces(): List<PlaceDto>

    @Query("SELECT * FROM ${PlaceDto.TABLE_NAME} WHERE placeId IN (:placesId)")
    fun getPlacesByIds(placesId: List<String>): List<PlaceDto>

    @Update
    fun updatePlace(placeDto: PlaceDto)

    @Delete
    fun deletePlace(placeDto: PlaceDto)
}