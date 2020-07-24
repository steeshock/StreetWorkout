package com.steeshock.android.streetworkout.data.repository

import androidx.lifecycle.LiveData
import com.steeshock.android.streetworkout.data.api.PlacesAPI
import com.steeshock.android.streetworkout.data.database.PlacesDao
import com.steeshock.android.streetworkout.data.model.Category
import com.steeshock.android.streetworkout.data.model.Place
import com.steeshock.android.streetworkout.data.model.State
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response

@ExperimentalCoroutinesApi
class Repository(
    private val placesDao: PlacesDao,
    private val placesAPI: PlacesAPI
) {

    val allPlaces: LiveData<List<Place>> = placesDao.getPlacesLive()
    val allFavoritePlaces: LiveData<List<Place>> = placesDao.getFavoritePlacesLive()

    fun getAllPlaces(): Flow<State<List<Place>>> {
        return object : NetworkBoundRepository<List<Place>, List<Place>>() {

            override suspend fun saveRemoteData(response: List<Place>) =
                placesDao.insertAllPlaces(response)

            override fun fetchFromLocal(): Flow<List<Place>> = placesDao.getPlaces()

            override suspend fun fetchFromRemote(): Response<List<Place>> = placesAPI.getPlaces()
        }.asFlow().flowOn(Dispatchers.IO)
    }

    fun getAllCategories(): Flow<State<List<Category>>> {
        return object : NetworkBoundRepository<List<Category>, List<Category>>() {

            override suspend fun saveRemoteData(response: List<Category>) =
                placesDao.insertAllCategories(response)

            override fun fetchFromLocal(): Flow<List<Category>> = placesDao.getCategories()

            override suspend fun fetchFromRemote(): Response<List<Category>> = placesAPI.getCategories()
        }.asFlow().flowOn(Dispatchers.IO)
    }

    fun insertPlace(place: Place) {
        placesDao.insertPlace(place)
    }

//    suspend fun updatePlaces() {
//        val response = placesAPI.getPlaces().body()
//        response?.let {
//            placesDao.updatePlaces(it)
//        }
//    }

    fun clearPlacesTable() {
        placesDao.clearPlacesTable()
    }

    fun removeAllPlacesExceptFavorites(boolean: Boolean) {
        placesDao.removeAllPlacesExceptFavorites(boolean)
    }

    companion object {

        @Volatile
        private var instance: Repository? = null

        fun getInstance(placesDao: PlacesDao, placesAPI: PlacesAPI) =
            instance
                ?: synchronized(this) {
                instance
                    ?: Repository(
                        placesDao,
                        placesAPI
                    )
                        .also { instance = it }
            }
    }
}