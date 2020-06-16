package com.example.android.streetworkout.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.streetworkout.data.Repository
import com.example.android.streetworkout.ui.places.PlacesViewModel

class CustomPlacesViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return PlacesViewModel(repository) as T
    }
}