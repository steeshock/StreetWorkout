package com.steeshock.android.streetworkout.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.steeshock.android.streetworkout.data.repository.interfaces.IPlacesRepository
import javax.inject.Inject

class ProfileViewModel(private val placesRepository: IPlacesRepository) : ViewModel() {
}