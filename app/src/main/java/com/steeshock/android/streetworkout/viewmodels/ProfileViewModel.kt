package com.steeshock.android.streetworkout.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.steeshock.android.streetworkout.data.repository.interfaces.IPlacesRepository
import javax.inject.Inject

class ProfileViewModel(private val placesRepository: IPlacesRepository) : ViewModel() {
}
class CustomProfileViewModelFactory @Inject constructor(
    private val placesRepository: IPlacesRepository
    ) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ProfileViewModel(
            placesRepository
        ) as T
    }
}