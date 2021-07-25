package com.steeshock.android.streetworkout.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.steeshock.android.streetworkout.data.api.APIResponse
import com.steeshock.android.streetworkout.data.model.Category
import com.steeshock.android.streetworkout.data.model.Place
import com.steeshock.android.streetworkout.data.repository.Repository
import io.reactivex.rxjava3.disposables.CompositeDisposable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PlacesViewModel(private val repository: Repository) : ViewModel() {

    val isLoading = MutableLiveData(false)
    //private val compositeDisposable = CompositeDisposable()

    val placesLiveData = repository.allPlaces
    val categoriesLiveData = repository.allCategories

    fun updatePlacesFromFirebase() {

        setLoading(true)

        val database = Firebase.database("https://test-projects-b523c-default-rtdb.europe-west1.firebasedatabase.app/")

        database.getReference("places").get().addOnSuccessListener {

            for (child in it.children) {

                val place = child.getValue<Place>()

                val isFavorite = placesLiveData.value?.find { p -> p.place_id == place?.place_id }?.isFavorite

                place?.isFavorite = isFavorite

                place?.let { i -> insertPlace(i) }
            }

            setLoading(false)

        }.addOnFailureListener{
            setLoading(false)
        }
    }

    fun updateCategoriesFromFirebase() {

        setLoading(true)

        val database = Firebase.database("https://test-projects-b523c-default-rtdb.europe-west1.firebasedatabase.app/")

        database.getReference("categories").get().addOnSuccessListener {

            for (child in it.children) {

                val category = child.getValue<Category>()

                val isSelected = categoriesLiveData.value?.find { p -> p.category_id == category?.category_id }?.isSelected

                category?.isSelected = isSelected

                category?.let { i -> insertCategory(i) }
            }

            setLoading(false)

        }.addOnFailureListener{
            setLoading(false)
        }
    }

//    fun updatePlaces() {
//        setLoading(true)
//        repository.updatePlaces(compositeDisposable, object :
//            APIResponse<List<Place>> {
//            override fun onSuccess(result: List<Place>?) {
//                setLoading(false)
//                result?.let { insertPlaces(it) }
//            }
//
//            override fun onError(t: Throwable) {
//                setLoading(false)
//                t.printStackTrace()
//            }
//        })
//    }
//
//    fun updateCategories() {
//        setLoading(true)
//        repository.updateCategories(compositeDisposable, object :
//            APIResponse<List<Category>> {
//            override fun onSuccess(result: List<Category>?) {
//                setLoading(false)
//                result?.let { insertCategories(it) }
//            }
//
//            override fun onError(t: Throwable) {
//                setLoading(false)
//                t.printStackTrace()
//            }
//        })
//    }

    fun insertPlaces(places: List<Place>) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertAllPlaces(places)
    }

    fun insertCategories(categories: List<Category>) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertAllCategories(categories)
    }

    fun setLoading(isVisible: Boolean) {
        this.isLoading.value = isVisible
    }

    fun clearDatabase() = viewModelScope.launch(Dispatchers.IO) {
        repository.clearDatabase()
    }

    fun insertPlace(place: Place) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertPlace(place)
    }

    fun insertCategory(category: Category) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertCategory(category)
    }

    fun updateCategory(category: Category) = viewModelScope.launch(Dispatchers.IO) {
        repository.updateCategory(category)
    }

    fun updatePlace(place: Place) = viewModelScope.launch(Dispatchers.IO) {
        repository.updatePlace(place)
    }

    fun removeAllPlacesExceptFavorites(boolean: Boolean) = viewModelScope.launch(Dispatchers.IO) {
        repository.removeAllPlacesExceptFavorites(boolean)
    }

    fun removeAllPlaces() = viewModelScope.launch(Dispatchers.IO) {
        repository.clearPlacesTable()
    }
}