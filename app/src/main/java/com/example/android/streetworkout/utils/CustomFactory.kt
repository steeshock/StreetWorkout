package com.example.android.streetworkout.utils

import androidx.annotation.NonNull
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.streetworkout.model.Storage
import com.example.android.streetworkout.ui.places.PlacesViewModel

class CustomFactory(storage: Storage?) :
    ViewModelProvider.NewInstanceFactory() {
    private val mStorage: Storage?

    @NonNull
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return PlacesViewModel(mStorage) as T
    }

    init {
        mStorage = storage
    }
}
