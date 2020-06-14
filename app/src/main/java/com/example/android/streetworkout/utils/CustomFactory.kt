package com.example.android.streetworkout.utils

import androidx.annotation.NonNull
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.streetworkout.data.Repository
import com.example.android.streetworkout.ui.places.PlacesViewModel

class CustomFactory(repository: Repository?) :
    ViewModelProvider.NewInstanceFactory() {
    private val mRepository: Repository?

    @NonNull
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return PlacesViewModel(mRepository) as T
    }

    init {
        mRepository = repository
    }
}
