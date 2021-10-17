package com.steeshock.android.streetworkout.data.repository.implementation.rxjava

import android.net.Uri
import androidx.lifecycle.LiveData
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.steeshock.android.streetworkout.data.api.APIResponse
import com.steeshock.android.streetworkout.data.api.PlacesAPI
import com.steeshock.android.streetworkout.data.database.PlacesDao
import com.steeshock.android.streetworkout.data.model.Place
import com.steeshock.android.streetworkout.data.repository.interfaces.IPlacesRepository
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import kotlinx.coroutines.tasks.await
import java.util.*

class RxJavaPlacesRepository(
    private val placesDao: PlacesDao,
    private val placesAPI: PlacesAPI
) : IPlacesRepository {

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    override val allPlaces: LiveData<List<Place>> = placesDao.getPlacesLive()
    override val allFavoritePlaces: LiveData<List<Place>> = placesDao.getFavoritePlacesLive()

    companion object {

        @Volatile
        private var instance: RxJavaPlacesRepository? = null

        fun getInstance(
            placesDao: PlacesDao,
            placesAPI: PlacesAPI
        ) =
            instance
                ?: synchronized(this) {
                    instance
                        ?: RxJavaPlacesRepository(
                            placesDao,
                            placesAPI
                        )
                            .also { instance = it }
                }
    }

    override suspend fun fetchPlaces(onResponse: APIResponse<List<Place>>) {
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