package com.steeshock.streetworkout.data.repository.implementation.mockApi

import android.net.Uri
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.steeshock.streetworkout.common.Constants.FIREBASE_PATH
import com.steeshock.streetworkout.data.api.PlacesAPI
import com.steeshock.streetworkout.data.database.PlacesDao
import com.steeshock.streetworkout.data.mappers.mapToDto
import com.steeshock.streetworkout.data.mappers.mapToEntity
import com.steeshock.streetworkout.interactor.entity.Place
import com.steeshock.streetworkout.interactor.repository.IPlacesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * Repository for work with REST endpoints
 */
open class SimpleApiPlacesRepository @Inject constructor(
    private val placesDao: PlacesDao,
    private val placesAPI: PlacesAPI
) : IPlacesRepository {

    override val allPlaces = placesDao.getPlacesFlow().map { places ->
        places.map { it.mapToEntity() }
    }
    override val allFavoritePlaces = placesDao.getFavoritePlacesFlow().map { places ->
        places.map { it.mapToEntity() }
    }

    override suspend fun fetchPlaces(): Boolean {
        val result = placesAPI.getPlaces()
        if (result.isSuccessful) {
            result.body()?.let { placesDao.insertAllPlaces(it) }
            return true
        }
        return false
    }

    override suspend fun getLocalFavorites(): List<String> {
        return placesDao.getFavoritePlaces().map { it.placeId }
    }

    /**
     * Firebase realization because of none implementation on Mock API
     */
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

    /**
     * Firebase realization because of none implementation on Mock API
     */
    override suspend fun insertPlaceRemote(newPlace: Place) {
        val database = Firebase.database(FIREBASE_PATH)
        val myRef = database.getReference("places").child(newPlace.placeId)
        myRef.setValue(newPlace).await()
    }

    override suspend fun updatePlace(place: Place) {
        placesDao.updatePlace(place.mapToDto())
    }

    override suspend fun deletePlace(place: Place): Boolean {
        TODO("Not yet implemented")
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
