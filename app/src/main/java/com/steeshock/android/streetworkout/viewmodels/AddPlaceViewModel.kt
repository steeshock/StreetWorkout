package com.steeshock.android.streetworkout.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.steeshock.android.streetworkout.data.model.Category
import com.steeshock.android.streetworkout.data.repository.Repository
import com.steeshock.android.streetworkout.data.model.Place
import com.steeshock.android.streetworkout.data.model.State
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

class AddPlaceViewModel(private val repository: Repository) : ViewModel() {

    val allCategoriesLive: LiveData<List<Category>> = repository.allCategories

    var checkedCategoriesArray: BooleanArray? = null
    var selectedCategories: ArrayList<Int> = arrayListOf()

    fun insert(place: Place) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertPlace(place)
    }
}