package com.steeshock.android.streetworkout.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.steeshock.android.streetworkout.data.model.Category
import com.steeshock.android.streetworkout.data.model.Place
import kotlinx.coroutines.flow.Flow


@Dao
interface PlacesDao {

    @Transaction
    fun updatePlaces(places: List<Place>) {
        removeAllPlacesExceptFavorites(false)
        insertAllPlaces(places)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPlace(place: Place)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAllPlaces(places: List<Place>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAllCategories(category: List<Category>)

    @Query("SELECT * FROM ${Place.TABLE_NAME}")
    fun getPlaces(): Flow<List<Place>>

    @Query("SELECT * FROM ${Category.TABLE_NAME}")
    fun getCategories(): Flow<List<Category>>

    @Query("SELECT * FROM ${Place.TABLE_NAME}")
    fun getPlacesLive(): LiveData<List<Place>>

    @Query("SELECT * FROM ${Place.TABLE_NAME} WHERE isFavorite")
    fun getFavoritePlacesLive(): LiveData<List<Place>>

    @Query("DELETE FROM ${Place.TABLE_NAME}")
    fun clearPlacesTable()

    @Query("DELETE FROM ${Place.TABLE_NAME} WHERE isFavorite = :boolean")
    fun removeAllPlacesExceptFavorites(boolean: Boolean)
}