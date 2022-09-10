package com.steeshock.streetworkout.data.dataSources.implementation.places

import android.net.Uri
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageException
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.steeshock.streetworkout.common.Constants
import com.steeshock.streetworkout.data.dataSources.interfaces.remote.IPlacesRemoteDataSource
import com.steeshock.streetworkout.data.repository.dto.PlaceDto
import com.steeshock.streetworkout.domain.entity.Place
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
 * Implementation for Firebase Realtime Database
 */
class FirebasePlacesRemoteDataSource @Inject constructor() : IPlacesRemoteDataSource {

    companion object {
        private const val PLACES_REMOTE_STORAGE = "places"
    }

    override suspend fun getPlacesRemote(): List<PlaceDto> {
        return suspendCoroutine { continuation ->
            val database = Firebase.database(Constants.FIREBASE_PATH)
            database.getReference(PLACES_REMOTE_STORAGE).get().addOnSuccessListener {
                CoroutineScope(Dispatchers.IO).launch {
                    val places = it.children.mapNotNull { child-> child.getValue<PlaceDto>()  }
                    continuation.resume(places)
                }
            }.addOnFailureListener {
                continuation.resumeWithException(it)
            }
        }
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

    override suspend fun insertPlaceRemote(newPlace: Place) {
        val database = Firebase.database(Constants.FIREBASE_PATH)
        val myRef = database.getReference(PLACES_REMOTE_STORAGE).child(newPlace.placeId)
        myRef.setValue(newPlace).await()
    }

    //TODO Do requests in parallel
    override suspend fun deletePlaceRemote(place: Place): Boolean {
        return suspendCoroutine { continuation ->
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    if (removePlaceRemote(place) && removeRelatedImagesRemote(place)) {
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

    private suspend fun removePlaceRemote(place: Place): Boolean {
        return suspendCoroutine { continuation ->
            val database = Firebase.database(Constants.FIREBASE_PATH)
            database.getReference(PLACES_REMOTE_STORAGE).child(place.placeId).get()
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