package com.steeshock.streetworkout.data.repository.implementation.firebase

import android.net.Uri
import androidx.lifecycle.LiveData
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.steeshock.streetworkout.common.Constants.FIREBASE_PATH
import com.steeshock.streetworkout.data.api.APIResponse
import com.steeshock.streetworkout.data.database.PlacesDao
import com.steeshock.streetworkout.data.model.Place
import com.steeshock.streetworkout.data.repository.interfaces.IPlacesRepository
import kotlinx.coroutines.tasks.await
import java.util.*

/**
 * Repository for work with Firebase Realtime Database
 */
open class FirebasePlacesRepository(
    private val placesDao: PlacesDao
) : IPlacesRepository {

    override val allPlaces: LiveData<List<Place>> = placesDao.getPlacesLive()
    override val allFavoritePlaces: LiveData<List<Place>> = placesDao.getFavoritePlacesLive()

    companion object {

        @Volatile
        private var instance: FirebasePlacesRepository? = null

        fun getInstance(placesDao: PlacesDao) =
            instance
                ?: synchronized(this) {
                    instance ?: FirebasePlacesRepository(placesDao).also { instance = it }
                }
    }

    override suspend fun fetchPlaces(onResponse: APIResponse<List<Place>>) {
        val database = Firebase.database(FIREBASE_PATH)
        val places: MutableList<Place> = mutableListOf()

        database.getReference("places").get().addOnSuccessListener {

            for (child in it.children) {
                val place = child.getValue<Place>()
                val isFavorite = allPlaces.value?.find { p -> p.placeId == place?.placeId }?.isFavorite
                place?.isFavorite = isFavorite == true
                place?.let { p -> places.add(p) }
            }

            onResponse.onSuccess(places)

        }.addOnFailureListener {
            onResponse.onError(it)
        }
    }

    override suspend fun uploadImage(uri: Uri, placeId: String?): Uri? {
        val reference = Firebase.storage.reference.child("${placeId}/image-${Date().time}.jpg")
        val uploadTask = reference.putFile(uri)

        uploadTask.await()
        return reference.downloadUrl.await()
    }

    override suspend fun insertPlaceLocal(newPlace: Place) {
        placesDao.insertPlace(newPlace)
    }

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