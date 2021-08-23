package com.steeshock.android.streetworkout.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.steeshock.android.streetworkout.data.repository.Repository
import com.steeshock.android.streetworkout.data.model.Place
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

class FavoritePlacesViewModel(private val repository: Repository) : ViewModel() {

    val allFavoritePlacesLive: LiveData<List<Place>> = repository.allFavoritePlaces

    fun removePlaceFromFavorites(place: Place) = viewModelScope.launch(Dispatchers.IO){
        place.changeFavoriteState()
        insertPlace(place)
    }

    fun returnPlaceToFavorites(place: Place) = viewModelScope.launch(Dispatchers.IO){
        place.changeFavoriteState()
        insertPlace(place)
    }

    private fun insertPlace(place: Place) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertPlace(place)
    }
}