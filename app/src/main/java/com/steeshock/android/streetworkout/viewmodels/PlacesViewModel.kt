package com.steeshock.android.streetworkout.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.steeshock.android.streetworkout.data.repository.Repository
import com.steeshock.android.streetworkout.data.model.Place
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PlacesViewModel(private val repository: Repository) : ViewModel() {

    val allPlacesLive: LiveData<List<Place>> = repository.allPlaces

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