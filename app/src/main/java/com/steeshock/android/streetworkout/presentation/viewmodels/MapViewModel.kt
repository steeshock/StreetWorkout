package com.steeshock.android.streetworkout.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.steeshock.android.streetworkout.data.model.Place
import com.steeshock.android.streetworkout.data.repository.interfaces.IPlacesRepository
import javax.inject.Inject

class MapViewModel @Inject constructor(
    placesRepository: IPlacesRepository,
) : ViewModel() {

    val allPlacesLive: LiveData<List<Place>> = placesRepository.allPlaces
}