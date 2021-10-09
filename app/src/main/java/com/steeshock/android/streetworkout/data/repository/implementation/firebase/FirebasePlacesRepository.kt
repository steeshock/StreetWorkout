package com.steeshock.android.streetworkout.data.repository.implementation.firebase

import android.net.Uri
import androidx.lifecycle.LiveData
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.steeshock.android.streetworkout.data.api.APIResponse
import com.steeshock.android.streetworkout.data.database.PlacesDao
import com.steeshock.android.streetworkout.data.model.Place
import com.steeshock.android.streetworkout.data.repository.interfaces.IPlacesRepository
import kotlinx.coroutines.tasks.await
import java.util.*

class FirebasePlacesRepository(
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
                    instance
                        ?: FirebasePlacesRepository(
                            placesDao,
                        )
                            .also { instance = it }
                }
    }

    override fun fetchPlaces(onResponse: APIResponse<List<Place>>) {
        val database =
            Firebase.database("https://test-projects-b523c-default-rtdb.europe-west1.firebasedatabase.app/")
        val places: MutableList<Place> = mutableListOf()

        database.getReference("places").get().addOnSuccessListener {

            for (child in it.children) {

                val place = child.getValue<Place>()

                val isFavorite =
                    allPlaces.value?.find { p -> p.place_uuid == place?.place_uuid }?.isFavorite

                place?.isFavorite = isFavorite

                place?.let { p -> places.add(p) }
            }

            onResponse.onSuccess(places)

        }.addOnFailureListener {
            onResponse.onError(it)
        }
    }

    override suspend fun uploadImage(uri: Uri, placeUUID: String): Uri? {
        val reference = Firebase.storage.reference.child("${placeUUID}/image-${Date().time}.jpg")
        val uploadTask = reference.putFile(uri)

        uploadTask.await()
        return reference.downloadUrl.await()
    }

    override suspend fun insertPlaceLocal(newPlace: Place) {
        placesDao.insertPlace(newPlace)
    }

    override suspend fun insertPlaceRemote(newPlace: Place) {
        val database =
            Firebase.database("https://test-projects-b523c-default-rtdb.europe-west1.firebasedatabase.app/")

        val myRef = database.getReference("places").child(newPlace.place_uuid)

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