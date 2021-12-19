package com.steeshock.android.streetworkout.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.steeshock.android.streetworkout.data.model.Place
import com.steeshock.android.streetworkout.data.repository.interfaces.IPlacesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FavoritePlacesViewModel(private val placesRepository: IPlacesRepository) : ViewModel() {

    val favoritePlacesLive: LiveData<List<Place>> = placesRepository.allFavoritePlaces

    fun removePlaceFromFavorites(place: Place) = viewModelScope.launch(Dispatchers.IO) {
        place.changeFavoriteState()
        insertPlace(place)
    }

    fun returnPlaceToFavorites(place: Place) = viewModelScope.launch(Dispatchers.IO) {
        place.changeFavoriteState()
        insertPlace(place)
    }

    private fun insertPlace(place: Place) = viewModelScope.launch(Dispatchers.IO) {
        placesRepository.insertPlaceLocal(place)
    }
}