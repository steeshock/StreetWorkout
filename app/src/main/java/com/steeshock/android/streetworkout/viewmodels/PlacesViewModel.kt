package com.steeshock.android.streetworkout.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.steeshock.android.streetworkout.data.model.Category
import com.steeshock.android.streetworkout.data.repository.Repository
import com.steeshock.android.streetworkout.data.model.Place
import com.steeshock.android.streetworkout.data.model.State
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class PlacesViewModel(private val repository: Repository) : ViewModel() {

    private val _placesLiveData = MutableLiveData<State<List<Place>>>()
    private val _categoriesLiveData = MutableLiveData<State<List<Category>>>()

    val placesLiveData: LiveData<State<List<Place>>>
        get() = _placesLiveData

    val categoriesLiveData: LiveData<State<List<Category>>>
        get() = _categoriesLiveData

    fun getPlaces() {
        viewModelScope.launch {
            repository.getAllPlaces().collect {
                _placesLiveData.value = it
            }
        }
    }

    fun getCategories() {
        viewModelScope.launch {
            repository.getAllCategories().collect {
                _categoriesLiveData.value = it
            }
        }
    }

    fun clearDatabase() = viewModelScope.launch(Dispatchers.IO) {
        repository.clearDatabase()
    }

    fun insertPlace(place: Place) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertPlace(place)
    }

    fun removeAllPlacesExceptFavorites(boolean: Boolean) = viewModelScope.launch(Dispatchers.IO) {
        repository.removeAllPlacesExceptFavorites(boolean)
    }

//    fun updatePlaces() {
//        viewModelScope.launch(Dispatchers.IO) {
//            repository.updatePlaces()
//        }
//    }
}