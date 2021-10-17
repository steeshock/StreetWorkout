package com.steeshock.android.streetworkout.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.steeshock.android.streetworkout.data.model.Place
import com.steeshock.android.streetworkout.data.repository.interfaces.IPlacesRepository
import javax.inject.Inject

class MapViewModel(placesRepository: IPlacesRepository) : ViewModel() {

    val allPlacesLive: LiveData<List<Place>> = placesRepository.allPlaces
}