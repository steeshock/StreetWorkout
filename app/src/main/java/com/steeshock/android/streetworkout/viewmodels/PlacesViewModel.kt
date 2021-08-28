package com.steeshock.android.streetworkout.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.steeshock.android.streetworkout.data.api.APIResponse
import com.steeshock.android.streetworkout.data.model.Category
import com.steeshock.android.streetworkout.data.model.Place
import com.steeshock.android.streetworkout.data.repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PlacesViewModel(private val repository: Repository) : ViewModel() {

    val isLoading = MutableLiveData(false)

    val placesLiveData = repository.allPlaces
    val categoriesLiveData = repository.allCategories

    fun fetchPlacesFromFirebase() = viewModelScope.launch(Dispatchers.IO) {

        setLoading(true)

        repository.fetchPlacesFromFirebase(object :
            APIResponse<List<Place>> {
            override fun onSuccess(result: List<Place>?) {
                result?.let { insertPlaces(it) }
            }

            override fun onError(t: Throwable) {
                setLoading(false)
                t.printStackTrace()
            }
        })
    }

    fun fetchCategoriesFromFirebase() = viewModelScope.launch(Dispatchers.IO) {

        setLoading(true)

        repository.fetchCategoriesFromFirebase(object :
            APIResponse<List<Category>> {
            override fun onSuccess(result: List<Category>?) {
                result?.let { insertCategories(it) }
            }

            override fun onError(t: Throwable) {
                setLoading(false)
                t.printStackTrace()
            }
        })
    }

    fun insertPlaces(places: List<Place>) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertAllPlaces(places)
        setLoading(false)
    }

    fun insertCategories(categories:List<Category>) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertAllCategories(categories)
        setLoading(false)
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

    fun clearDatabase() = viewModelScope.launch(Dispatchers.IO) {
        repository.clearDatabase()
    }

    fun setLoading(isVisible: Boolean) {
        this.isLoading.postValue(isVisible)
    }
}