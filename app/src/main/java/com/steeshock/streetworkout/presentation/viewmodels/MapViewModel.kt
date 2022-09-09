package com.steeshock.streetworkout.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.steeshock.streetworkout.interactor.entity.Place
import com.steeshock.streetworkout.interactor.repository.IPlacesRepository
import javax.inject.Inject

class MapViewModel @Inject constructor(
    placesRepository: IPlacesRepository,
) : ViewModel() {

    val observablePlaces: LiveData<List<Place>> = placesRepository.allPlaces.asLiveData()
}