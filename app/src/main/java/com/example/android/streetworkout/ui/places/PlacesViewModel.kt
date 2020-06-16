package com.example.android.streetworkout.ui.places

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.android.streetworkout.AppDelegate
import com.example.android.streetworkout.data.model.PlaceObject
import com.example.android.streetworkout.data.Repository
import com.example.android.streetworkout.data.database.PlacesDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PlacesViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: Repository = (application as AppDelegate).getRepository()!!

    val allPlacesLive: LiveData<List<PlaceObject>> = repository.allPlaces

    fun insert(place: PlaceObject) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertPlace(place)
    }

    fun clearPlacesTable() = viewModelScope.launch(Dispatchers.IO) {
        repository.clearPlacesTable()
    }

    fun getAllPlacesSize() = repository.getAllPlaces().size
}