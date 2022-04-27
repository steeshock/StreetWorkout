package com.steeshock.streetworkout.data.repository.implementation.mockApi

import android.net.Uri
import androidx.lifecycle.LiveData
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.steeshock.streetworkout.common.Constants.FIREBASE_PATH
import com.steeshock.streetworkout.data.api.APIResponse
import com.steeshock.streetworkout.data.api.PlacesAPI
import com.steeshock.streetworkout.data.database.PlacesDao
import com.steeshock.streetworkout.data.model.Place
import com.steeshock.streetworkout.data.repository.interfaces.IPlacesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.util.*

/**
 * Repository for work with REST endpoints
 */
open class MockApiPlacesRepository(
    private val placesDao: PlacesDao,
    private val placesAPI: PlacesAPI
) : IPlacesRepository {

    override val allPlaces: LiveData<List<Place>> = placesDao.getPlacesLive()
    override val allFavoritePlaces: LiveData<List<Place>> = placesDao.getFavoritePlacesLive()

    override suspend fun fetchPlaces(onResponse: APIResponse<List<Place>>) {
        var result: Response<List<Place>>? = null
        try {
            result = placesAPI.getPlaces()
        } catch (t: Throwable) {
            onResponse.onError(t)
        }
        withContext(Dispatchers.Main) {
            if (result?.isSuccessful == true) {
                onResponse.onSuccess(result.body())
            }
        }
    }

    /**
     * Firebase realization because of none implementation on Mock API
     */
    override suspend fun uploadImage(uri: Uri, placeId: String?): Uri? {
        val reference = Firebase.storage.reference.child("${placeId}/image-${Date().time}.jpg")
        val uploadTask = reference.putFile(uri)

        uploadTask.await()
        return reference.downloadUrl.await()
    }

    override suspend fun insertPlaceLocal(newPlace: Place) {
        placesDao.insertPlace(newPlace)
    }

    /**
     * Firebase realization because of none implementation on Mock API
     */
    override suspend fun insertPlaceRemote(newPlace: Place) {
        val database = Firebase.database(FIREBASE_PATH)
        val myRef = database.getReference("places").child(newPlace.placeId)
        myRef.setValue(newPlace).await()
    }

    override suspend fun insertAllPlaces(places: List<Place>) {
        placesDao.insertAllPlaces(places)
    }

    override suspend fun updatePlace(place: Place) {
        placesDao.updatePlace(place)
    }

    override suspend fun clearPlacesTable() {
        placesDao.clearPlacesTable()
    }
}
