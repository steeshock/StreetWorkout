package com.steeshock.android.streetworkout.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.steeshock.android.streetworkout.data.Repository
import com.steeshock.android.streetworkout.data.model.PlaceObject

class MapViewModel(private val repository: Repository) : ViewModel() {

    val allPlacesLive: LiveData<List<PlaceObject>> = repository.allPlaces
}