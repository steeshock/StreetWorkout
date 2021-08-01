package com.steeshock.android.streetworkout.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.steeshock.android.streetworkout.data.repository.Repository
import com.steeshock.android.streetworkout.data.model.Place
import kotlinx.coroutines.ExperimentalCoroutinesApi

class MapViewModel(private val repository: Repository) : ViewModel() {

    val allPlacesLive: LiveData<List<Place>> = repository.allPlaces
}