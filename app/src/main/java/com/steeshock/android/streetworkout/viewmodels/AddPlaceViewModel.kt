package com.steeshock.android.streetworkout.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.steeshock.android.streetworkout.data.Repository
import com.steeshock.android.streetworkout.data.model.PlaceObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddPlaceViewModel(private val repository: Repository) : ViewModel() {
    fun insert(place: PlaceObject) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertPlace(place)
    }
}