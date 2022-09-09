package com.steeshock.streetworkout.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.steeshock.streetworkout.data.model.CategoryDto
import com.steeshock.streetworkout.data.model.PlaceDto
import kotlinx.coroutines.flow.Flow


@Dao
interface PlacesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPlace(placeDto: PlaceDto)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllPlaces(places: List<PlaceDto>)

    @Query("SELECT * FROM ${PlaceDto.TABLE_NAME}")
    fun getPlacesFlow(): Flow<List<PlaceDto>>

    @Query("SELECT * FROM ${PlaceDto.TABLE_NAME} WHERE isFavorite")
    fun getFavoritePlacesFlow(): Flow<List<PlaceDto>>

    @Query("DELETE FROM ${PlaceDto.TABLE_NAME}")
    fun clearPlacesTable()

    @Query("SELECT * FROM ${PlaceDto.TABLE_NAME}")
    fun getAllPlaces(): List<PlaceDto>

    @Query("SELECT * FROM ${PlaceDto.TABLE_NAME} WHERE isFavorite")
    fun getFavoritePlaces(): List<PlaceDto>

    @Query("SELECT * FROM ${PlaceDto.TABLE_NAME} WHERE placeId = :placeId")
    fun getPlaceById(placeId: String?): PlaceDto?

    @Update
    fun updatePlace(placeDto: PlaceDto)

    @Delete
    fun deletePlace(placeDto: PlaceDto)
}