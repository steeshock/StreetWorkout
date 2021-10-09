package com.steeshock.android.streetworkout.utils.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.steeshock.android.streetworkout.data.repository.interfaces.IPlacesRepository
import com.steeshock.android.streetworkout.viewmodels.ProfileViewModel

class CustomProfileViewModelFactory(private val placesRepository: IPlacesRepository) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ProfileViewModel(
            placesRepository
        ) as T
    }
}