package com.steeshock.streetworkout.data.repository.implementation.firebase

import android.net.Uri
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageException
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.steeshock.streetworkout.common.Constants.FIREBASE_PATH
import com.steeshock.streetworkout.data.database.PlacesDao
import com.steeshock.streetworkout.data.mappers.mapToDto
import com.steeshock.streetworkout.data.mappers.mapToEntity
import com.steeshock.streetworkout.data.model.PlaceDto
import com.steeshock.streetworkout.domain.entity.Place
import com.steeshock.streetworkout.domain.repository.IPlacesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
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

    override val allPlaces = placesDao.getPlacesFlow().map { places ->
        places.map { it.mapToEntity() }
    }
    override val allFavoritePlaces = placesDao.getFavoritePlacesFlow().map { places ->
            places.map { it.mapToEntity() }
    }

    override suspend fun fetchPlaces(): Boolean {
        return suspendCoroutine { continuation ->
            val database = Firebase.database(FIREBASE_PATH)
            val places: MutableList<PlaceDto> = mutableListOf()
            database.getReference("places").get().addOnSuccessListener {
                CoroutineScope(Dispatchers.IO).launch {
                    for (child in it.children) {
                        val place = child.getValue<PlaceDto>()
                        val isFavorite = placesDao.getPlaceById(place?.placeId)?.isFavorite
                        place?.isFavorite = isFavorite == true
                        place?.let { p -> places.add(p) }
                    }
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

    override suspend fun uploadImage(uri: String, placeId: String?): String? {
        return suspendCoroutine { continuation ->
            val reference = Firebase.storage.reference.child("${placeId}/image-${Date().time}.jpg")
            CoroutineScope(Dispatchers.IO).launch {
                reference.putFile(Uri.parse(uri)).await()
                reference.downloadUrl
                    .addOnSuccessListener {
                        continuation.resume(it.toString())
                    }
                    .addOnFailureListener {
                        continuation.resumeWithException(it)
                    }
            }
        }
    }

    override suspend fun insertPlaceLocal(newPlace: Place) {
        placesDao.insertPlace(newPlace.mapToDto())
    }

    override suspend fun insertPlaceRemote(newPlace: Place) {
        val database = Firebase.database(FIREBASE_PATH)
        val myRef = database.getReference("places").child(newPlace.placeId)
        myRef.setValue(newPlace).await()
    }

    override suspend fun updatePlace(place: Place) {
        placesDao.updatePlace(place.mapToDto())
    }

    //TODO Do requests in parallel
    override suspend fun deletePlace(place: Place): Boolean {
        return suspendCoroutine { continuation ->
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    if (removePlaceRemote(place) && removeRelatedImagesRemote(place)) {
                        placesDao.deletePlace(place.mapToDto())
                        continuation.resume(true)
                    } else {
                        continuation.resume(false)
                    }
                } catch (e: Exception) {
                    continuation.resumeWithException(e)
                }
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

    private suspend fun removePlaceRemote(place: Place): Boolean {
        return suspendCoroutine { continuation ->
            val database = Firebase.database(FIREBASE_PATH)
            database.getReference("places").child(place.placeId).get()
                .addOnSuccessListener { dataSnapshot ->
                    dataSnapshot.ref.removeValue().addOnSuccessListener {
                        CoroutineScope(Dispatchers.IO).launch {
                            continuation.resume(true)
                        }
                    }
                }
                .addOnFailureListener {
                    continuation.resumeWithException(it)
                }
        }
    }

    /**
     * Try to delete place's related images from Firebase Storage
     * If images doesn't exist - Firebase returns error code -13010
     */
    private suspend fun removeRelatedImagesRemote(place: Place): Boolean {
        return suspendCoroutine { continuation ->
            Firebase.storage.reference.child(place.placeId).listAll()
                .addOnSuccessListener { images ->
                    CoroutineScope(Dispatchers.IO).launch {
                        images.items.forEach { deleteSingleImage(it) }
                        continuation.resume(true)
                    }
                }
                .addOnFailureListener {
                    if ((it as? StorageException)?.errorCode == StorageException.ERROR_OBJECT_NOT_FOUND) {
                        continuation.resume(true)
                    } else {
                        continuation.resumeWithException(it)
                    }
                }
        }
    }

    private suspend fun deleteSingleImage(storageReference: StorageReference): Boolean {
        return suspendCoroutine { continuation ->
            storageReference.delete()
                .addOnSuccessListener {
                    continuation.resume(true)
                }
                .addOnFailureListener {
                    continuation.resumeWithException(it)
                }
        }
    }
}