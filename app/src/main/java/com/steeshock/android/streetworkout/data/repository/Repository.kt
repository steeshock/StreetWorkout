package com.steeshock.android.streetworkout.data.repository

import android.net.Uri
import androidx.lifecycle.LiveData
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.steeshock.android.streetworkout.data.api.APIResponse
import com.steeshock.android.streetworkout.data.api.PlacesAPI
import com.steeshock.android.streetworkout.data.database.PlacesDao
import com.steeshock.android.streetworkout.data.model.Category
import com.steeshock.android.streetworkout.data.model.Place
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import kotlinx.coroutines.tasks.await
import java.util.*

class Repository(
    private val placesDao: PlacesDao,
    private val placesAPI: PlacesAPI
) {

    val allPlaces: LiveData<List<Place>> = placesDao.getPlacesLive()
    val allFavoritePlaces: LiveData<List<Place>> = placesDao.getFavoritePlacesLive()
    val allCategories: LiveData<List<Category>> = placesDao.getCategoriesLive()

    suspend fun uploadImageToFirebase(uri: Uri, placeUUID: String): Uri? {
        val reference = Firebase.storage.reference.child("${placeUUID}/image-${Date().time}.jpg")
        val uploadTask = reference.putFile(uri)
        uploadTask.await()
        return reference.downloadUrl.await()
    }

    fun updatePlaces(
        compositeDisposable: io.reactivex.rxjava3.disposables.CompositeDisposable,
        onResponse: APIResponse<List<Place>>
    ): io.reactivex.rxjava3.disposables.Disposable {
        return placesAPI.getPlaces()
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

    fun updateCategories(
        compositeDisposable: io.reactivex.rxjava3.disposables.CompositeDisposable,
        onResponse: APIResponse<List<Category>>
    ): io.reactivex.rxjava3.disposables.Disposable {
        return placesAPI.getCategories()
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

    fun insertAllPlaces(places: List<Place>) {
        placesDao.insertAllPlaces(places)
    }

    fun insertAllCategories(categories: List<Category>) {
        placesDao.insertAllCategories(categories)
    }

    fun insertPlace(place: Place) {
        placesDao.insertPlace(place)
    }

    fun insertCategory(category: Category) {
        placesDao.insertCategory(category)
    }

    fun updateCategory(category: Category) {
        placesDao.updateCategory(category)
    }

    fun updatePlace(place: Place) {
        placesDao.updatePlace(place)
    }

    fun clearPlacesTable() {
        placesDao.clearPlacesTable()
    }

    fun clearCategoriesTable() {
        placesDao.clearCategoriesTable()
    }

    fun clearDatabase() {
        placesDao.clearDatabase()
    }

    fun removeAllPlacesExceptFavorites(boolean: Boolean) {
        placesDao.removeAllPlacesExceptFavorites(boolean)
    }

    companion object {

        @Volatile
        private var instance: Repository? = null

        fun getInstance(placesDao: PlacesDao, placesAPI: PlacesAPI) =
            instance
                ?: synchronized(this) {
                instance
                    ?: Repository(
                        placesDao,
                        placesAPI
                    )
                        .also { instance = it }
            }
    }
}