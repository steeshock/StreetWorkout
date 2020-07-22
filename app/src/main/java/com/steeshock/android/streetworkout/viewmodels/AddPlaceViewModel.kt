package com.steeshock.android.streetworkout.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.steeshock.android.streetworkout.data.repository.Repository
import com.steeshock.android.streetworkout.data.model.Place
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddPlaceViewModel(private val repository: Repository) : ViewModel() {
    fun insert(place: Place) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertPlace(place)
    }
}