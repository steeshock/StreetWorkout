package com.steeshock.android.streetworkout.presentation.viewmodels

import androidx.lifecycle.ViewModel
import com.steeshock.android.streetworkout.data.repository.interfaces.IPlacesRepository
import javax.inject.Inject

class ProfileViewModel @Inject constructor(
    private val placesRepository: IPlacesRepository,
) : ViewModel() {
}