package com.steeshock.streetworkout.data.repository.implementation.firebase

import android.net.Uri
import androidx.lifecycle.LiveData
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.steeshock.streetworkout.common.Constants.FIREBASE_PATH
import com.steeshock.streetworkout.data.database.PlacesDao
import com.steeshock.streetworkout.data.model.Place
import com.steeshock.streetworkout.data.repository.interfaces.IPlacesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * Repository for work with Firebase Realtime Database
 */
open class FirebasePlacesRepository @Inject constructor(
    private val placesDao: PlacesDao,
) : IPlacesRepository {

    override val allPlaces: LiveData<List<Place>> = placesDao.getPlacesLive()
    override val allFavoritePlaces: LiveData<List<Place>> = placesDao.getFavoritePlacesLive()

    override suspend fun fetchPlaces(): Boolean {
        return suspendCoroutine { continuation ->
            val database = Firebase.database(FIREBASE_PATH)
            val places: MutableList<Place> = mutableListOf()
            database.getReference("places").get().addOnSuccessListener {
                for (child in it.children) {
                    val place = child.getValue<Place>()
                    val isFavorite = allPlaces.value?.find { p -> p.placeId == place?.placeId }?.isFavorite
                    place?.isFavorite = isFavorite == true
                    place?.let { p -> places.add(p) }
                }
                CoroutineScope(Dispatchers.IO).launch {
                    placesDao.insertAllPlaces(places)
                    continuation.resume(true)
                }
            }.addOnFailureListener {
                continuation.resumeWithException(it)
            }
        }
    }

    override suspend fun getLocalFavorites(): List<String> {
        return placesDao.getFavoritePlaces().map { it.placeId }
    }

    override suspend fun uploadImage(uri: Uri, placeId: String?): Uri? {
        return suspendCoroutine { continuation ->
            val reference = Firebase.storage.reference.child("${placeId}/image-${Date().time}.jpg")
            CoroutineScope(Dispatchers.IO).launch {
                reference.putFile(uri).await()
                reference.downloadUrl
                    .addOnSuccessListener {
                        continuation.resume(it)
                    }
                    .addOnFailureListener {
                        continuation.resumeWithException(it)
                    }
            }
        }
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

    override suspend fun deletePlace(place: Place): Boolean {
        return suspendCoroutine { continuation ->
            val database = Firebase.database(FIREBASE_PATH)
            database.getReference("places").child(place.placeId).get()
                .addOnSuccessListener { dataSnapshot ->
                    dataSnapshot.ref.removeValue().addOnSuccessListener {
                        CoroutineScope(Dispatchers.IO).launch {
                            placesDao.deletePlace(place)
                            continuation.resume(true)
                        }
                    }
                }
                .addOnFailureListener {
                    continuation.resumeWithException(it)
                }
        }
    }

    override suspend fun clearPlacesTable() {
        placesDao.clearPlacesTable()
    }

    override suspend fun updatePlacesWithFavoriteList(favorites: List<String>) {
        placesDao.getAllPlaces().apply {
            forEach { it.isFavorite = favorites.contains(it.placeId) }
            placesDao.insertAllPlaces(this)
        }
    }

    override suspend fun resetFavorites() {
        placesDao.getAllPlaces().apply {
            forEach { it.isFavorite = false }
            placesDao.insertAllPlaces(this)
        }
    }
}