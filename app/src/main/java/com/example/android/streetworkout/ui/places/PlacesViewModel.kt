package com.example.android.streetworkout.ui.places

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.streetworkout.data.model.PlaceObject
import com.example.android.streetworkout.data.Repository
import com.example.android.streetworkout.data.database.PlacesDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PlacesViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: Repository
    // Using LiveData and caching what getAlphabetizedWords returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.
    val allPlaces: LiveData<List<PlaceObject>>

    init {
        val placesDao = PlacesDatabase.getInstance(application).placesDao()
        repository = Repository(placesDao)
        allPlaces = repository.allPlaces
    }

    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun insert(place: PlaceObject) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertPlace(place)
    }

    fun clearPlacesTable() = viewModelScope.launch(Dispatchers.IO) {
        repository.clearPlacesTable()
    }
}