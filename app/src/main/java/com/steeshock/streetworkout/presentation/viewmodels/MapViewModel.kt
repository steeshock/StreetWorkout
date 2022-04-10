package com.steeshock.streetworkout.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.steeshock.streetworkout.data.model.Place
import com.steeshock.streetworkout.data.repository.interfaces.IPlacesRepository
import javax.inject.Inject

class MapViewModel @Inject constructor(
    placesRepository: IPlacesRepository,
) : ViewModel() {

    val observablePlaces: LiveData<List<Place>> = placesRepository.allPlaces
}