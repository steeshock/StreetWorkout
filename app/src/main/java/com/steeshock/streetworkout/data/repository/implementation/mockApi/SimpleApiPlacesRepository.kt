package com.steeshock.streetworkout.data.repository.implementation.mockApi

import android.net.Uri
import androidx.lifecycle.LiveData
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.steeshock.streetworkout.common.Constants.FIREBASE_PATH
import com.steeshock.streetworkout.data.api.PlacesAPI
import com.steeshock.streetworkout.data.database.PlacesDao
import com.steeshock.streetworkout.data.model.PlaceDto
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
 * Repository for work with REST endpoints
 */
open class SimpleApiPlacesRepository @Inject constructor(
    private val placesDao: PlacesDao,
    private val placesAPI: PlacesAPI
) : IPlacesRepository {

    override val allPlaces: LiveData<List<PlaceDto>> = placesDao.getPlacesLive()
    override val allFavoritePlaces: LiveData<List<PlaceDto>> = placesDao.getFavoritePlacesLive()

    override suspend fun fetchPlaces(): Boolean {
        val result = placesAPI.getPlaces()
        if (result.isSuccessful) {
            result.body()?.let { insertAllPlaces(it) }
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

    /**
     * Firebase realization because of none implementation on Mock API
     */
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

    override suspend fun deletePlace(placeDto: PlaceDto): Boolean {
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
