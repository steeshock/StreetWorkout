package com.steeshock.android.streetworkout.data.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.steeshock.android.streetworkout.data.repository.interfaces.IPlacesRepository
import com.steeshock.android.streetworkout.presentation.viewmodels.FavoritePlacesViewModel
import javax.inject.Inject

class FavoritePlacesViewModelFactory @Inject constructor(
    private val repository: IPlacesRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FavoritePlacesViewModel(
            repository
        ) as T
    }
}