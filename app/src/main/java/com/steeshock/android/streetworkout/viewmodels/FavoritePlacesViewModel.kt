package com.steeshock.android.streetworkout.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.steeshock.android.streetworkout.data.Repository
import com.steeshock.android.streetworkout.data.model.PlaceObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FavoritePlacesViewModel(private val repository: Repository) : ViewModel() {

    val allFavoritePlacesLive: LiveData<List<PlaceObject>> = repository.allFavoritePlaces

    fun insertPlace(place: PlaceObject) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertPlace(place)
    }
}