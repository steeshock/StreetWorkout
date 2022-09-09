package com.steeshock.streetworkout.data.repository.implementation.firebase

import android.net.Uri
import androidx.lifecycle.LiveData
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageException
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.steeshock.streetworkout.common.Constants.FIREBASE_PATH
import com.steeshock.streetworkout.data.database.PlacesDao
import com.steeshock.streetworkout.data.model.PlaceDto
import com.steeshock.streetworkout.data.repository.interfaces.IPlacesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.lang.Exception
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

    override val allPlaces: LiveData<List<PlaceDto>> = placesDao.getPlacesLive()
    override val allFavoritePlaces: LiveData<List<PlaceDto>> = placesDao.getFavoritePlacesLive()

    override suspend fun fetchPlaces(): Boolean {
        return suspendCoroutine { continuation ->
            val database = Firebase.database(FIREBASE_PATH)
            val placeDtos: MutableList<PlaceDto> = mutableListOf()
            database.getReference("places").get().addOnSuccessListener {
                for (child in it.children) {
                    val place = child.getValue<PlaceDto>()
                    val isFavorite = allPlaces.value?.find { p -> p.placeId == place?.placeId }?.isFavorite
                    place?.isFavorite = isFavorite == true
                    place?.let { p -> placeDtos.add(p) }
                }
                CoroutineScope(Dispatchers.IO).launch {
                    placesDao.insertAllPlaces(placeDtos)
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

    override suspend fun insertPlaceLocal(newPlaceDto: PlaceDto) {
        placesDao.insertPlace(newPlaceDto)
    }

    override suspend fun insertPlaceRemote(newPlaceDto: PlaceDto) {
        val database = Firebase.database(FIREBASE_PATH)
        val myRef = database.getReference("places").child(newPlaceDto.placeId)
        myRef.setValue(newPlaceDto).await()
    }

    override suspend fun insertAllPlaces(placeDtos: List<PlaceDto>) {
        placesDao.insertAllPlaces(placeDtos)
    }

    override suspend fun updatePlace(placeDto: PlaceDto) {
        placesDao.updatePlace(placeDto)
    }

    //TODO Do requests in parallel
    override suspend fun deletePlace(placeDto: PlaceDto): Boolean {
        return suspendCoroutine { continuation ->
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    if (removePlaceRemote(placeDto) && removeRelatedImagesRemote(placeDto)) {
                        placesDao.deletePlace(placeDto)
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

    private suspend fun removePlaceRemote(placeDto: PlaceDto): Boolean {
        return suspendCoroutine { continuation ->
            val database = Firebase.database(FIREBASE_PATH)
            database.getReference("places").child(placeDto.placeId).get()
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
    private suspend fun removeRelatedImagesRemote(placeDto: PlaceDto): Boolean {
        return suspendCoroutine { continuation ->
            Firebase.storage.reference.child(placeDto.placeId).listAll()
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