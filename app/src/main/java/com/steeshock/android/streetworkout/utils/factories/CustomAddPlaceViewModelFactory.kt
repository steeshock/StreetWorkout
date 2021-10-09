package com.steeshock.android.streetworkout.utils.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.steeshock.android.streetworkout.data.repository.interfaces.ICategoriesRepository
import com.steeshock.android.streetworkout.data.repository.interfaces.IPlacesRepository
import com.steeshock.android.streetworkout.viewmodels.AddPlaceViewModel

class CustomAddPlaceViewModelFactory(
    private val placesRepository: IPlacesRepository,
    private val categoriesRepository: ICategoriesRepository,
) :
    ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AddPlaceViewModel(
            placesRepository,
            categoriesRepository,
        ) as T
    }
}