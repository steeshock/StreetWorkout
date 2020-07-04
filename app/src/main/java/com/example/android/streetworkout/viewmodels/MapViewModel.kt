package com.example.android.streetworkout.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.android.streetworkout.data.Repository
import com.example.android.streetworkout.data.model.PlaceObject

class MapViewModel(private val repository: Repository) : ViewModel() {

    val allPlacesLive: LiveData<List<PlaceObject>> = repository.allPlaces
}