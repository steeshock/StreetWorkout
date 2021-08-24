package com.steeshock.android.streetworkout.data.repository

import android.net.Uri
import androidx.lifecycle.LiveData
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.steeshock.android.streetworkout.data.api.APIResponse
import com.steeshock.android.streetworkout.data.api.PlacesAPI
import com.steeshock.android.streetworkout.data.database.PlacesDao
import com.steeshock.android.streetworkout.data.model.Category
import com.steeshock.android.streetworkout.data.model.Place
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.*

class Repository(
    private val placesDao: PlacesDao,
    private val placesAPI: PlacesAPI
) {

    val allPlaces: LiveData<List<Place>> = placesDao.getPlacesLive()
    val allFavoritePlaces: LiveData<List<Place>> = placesDao.getFavoritePlacesLive()
    val allCategories: LiveData<List<Category>> = placesDao.getCategoriesLive()

    fun fetchPlacesFromFirebase(onResponse: APIResponse<List<Place>>){

        val database = Firebase.database("https://test-projects-b523c-default-rtdb.europe-west1.firebasedatabase.app/")
        val places: MutableList<Place> = mutableListOf()

        database.getReference("places").get().addOnSuccessListener {

            for (child in it.children) {

                val place = child.getValue<Place>()

                val isFavorite = allPlaces.value?.find { p -> p.place_uuid == place?.place_uuid }?.isFavorite

                place?.isFavorite = isFavorite

                place?.let { p -> places.add(p) }
            }

            onResponse.onSuccess(places)

        }.addOnFailureListener{
            onResponse.onError(it)
        }
    }

    fun fetchCategoriesFromFirebase(onResponse: APIResponse<List<Category>>){

        val database = Firebase.database("https://test-projects-b523c-default-rtdb.europe-west1.firebasedatabase.app/")
        val categories: MutableList<Category> = mutableListOf()

        database.getReference("categories").get().addOnSuccessListener {

            for (child in it.children) {

                val category = child.getValue<Category>()

                val isSelected = allCategories.value?.find { p -> p.category_id == category?.category_id }?.isSelected

                category?.isSelected = isSelected

                category?.let { c -> categories.add(c) }
            }

            onResponse.onSuccess(categories)

        }.addOnFailureListener{
            onResponse.onError(it)
        }
    }

    suspend fun uploadImageToFirebase(uri: Uri, placeUUID: String): Uri? {
        val reference = Firebase.storage.reference.child("${placeUUID}/image-${Date().time}.jpg")
        val uploadTask = reference.putFile(uri)

        uploadTask.await()
        return reference.downloadUrl.await()
    }

    suspend fun insertNewPlaceInFirebase(newPlace: Place) {

        val database = Firebase.database("https://test-projects-b523c-default-rtdb.europe-west1.firebasedatabase.app/")

        val myRef = database.getReference("places").child(newPlace.place_uuid)

        myRef.setValue(newPlace).await()
    }

    suspend fun insertAllPlaces(places: List<Place>) {
        placesDao.insertAllPlaces(places)
    }

    suspend fun insertAllCategories(categories: List<Category>) {
        placesDao.insertAllCategories(categories)
    }

    suspend fun insertPlace(place: Place) {
        placesDao.insertPlace(place)
    }

    suspend fun insertCategory(category: Category) {
        placesDao.insertCategory(category)
    }

    suspend fun updateCategory(category: Category) {
        placesDao.updateCategory(category)
    }

    suspend fun updatePlace(place: Place) {
        placesDao.updatePlace(place)
    }

    suspend fun clearPlacesTable() {
        placesDao.clearPlacesTable()
    }

    suspend fun clearCategoriesTable() {
        placesDao.clearCategoriesTable()
    }

    suspend fun clearDatabase() {
        placesDao.clearDatabase()
    }

    suspend fun removeAllPlacesExceptFavorites(boolean: Boolean) {
        placesDao.removeAllPlacesExceptFavorites(boolean)
    }

    //region RX Java approach
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
    //endregion

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