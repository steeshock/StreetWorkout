package com.steeshock.android.streetworkout.data.repository.implementation.mockApi

import android.net.Uri
import androidx.lifecycle.LiveData
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.steeshock.android.streetworkout.common.Constants.FIREBASE_PATH
import com.steeshock.android.streetworkout.data.api.APIResponse
import com.steeshock.android.streetworkout.data.api.PlacesAPI
import com.steeshock.android.streetworkout.data.database.PlacesDao
import com.steeshock.android.streetworkout.data.model.Place
import com.steeshock.android.streetworkout.data.repository.interfaces.IPlacesRepository
import io.reactivex.rxjava3.disposables.CompositeDisposable
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

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    override val allPlaces: LiveData<List<Place>> = placesDao.getPlacesLive()
    override val allFavoritePlaces: LiveData<List<Place>> = placesDao.getFavoritePlacesLive()

    companion object {

        @Volatile
        private var instance: MockApiPlacesRepository? = null

        fun getInstance(
            placesDao: PlacesDao,
            placesAPI: PlacesAPI
        ) =
            instance
                ?: synchronized(this) {
                    instance
                        ?: MockApiPlacesRepository(
                            placesDao,
                            placesAPI
                        )
                            .also { instance = it }
                }
    }

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
        /**
         * Obsolete RxJava approach
         */
        /*
        placesAPI.getPlaces()
            .subscribeOn(io.reactivex.rxjava3.schedulers.Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                onResponse.onSuccess(it)
            }, {
                onResponse.onError(it)
            }).also {
                compositeDisposable.add(it)
            }

         */
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
        val myRef = database.getReference("places").child(newPlace.place_id)
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
