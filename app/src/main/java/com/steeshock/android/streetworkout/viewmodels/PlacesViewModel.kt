package com.steeshock.android.streetworkout.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    val placesLiveData: LiveData<State<List<Place>>>
        get() = _placesLiveData

    fun getPlaces() {
        viewModelScope.launch {
            repository.getAllPlaces().collect {
                _placesLiveData.value = it
            }
        }
    }

    fun clearPlacesTable() = viewModelScope.launch(Dispatchers.IO) {
        repository.clearPlacesTable()
    }

    fun insertPlace(place: Place) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertPlace(place)
    }

    fun removeAllPlacesExceptFavorites(boolean: Boolean) = viewModelScope.launch(Dispatchers.IO) {
        repository.removeAllPlacesExceptFavorites(boolean)
    }

    fun updatePlaces() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updatePlaces()
        }
    }
}