package com.steeshock.android.streetworkout.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.steeshock.android.streetworkout.data.api.APIResponse
import com.steeshock.android.streetworkout.data.model.Category
import com.steeshock.android.streetworkout.data.model.Place
import com.steeshock.android.streetworkout.data.repository.interfaces.ICategoriesRepository
import com.steeshock.android.streetworkout.data.repository.interfaces.IPlacesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class PlacesViewModel(
    private val placesRepository: IPlacesRepository,
    private val categoriesRepository: ICategoriesRepository,
) : ViewModel() {

    val isLoading = MutableLiveData(false)

    val placesLiveData = placesRepository.allPlaces
    val categoriesLiveData = categoriesRepository.allCategories

    fun fetchPlacesFromFirebase() = viewModelScope.launch(Dispatchers.IO) {

        setLoading(true)

        placesRepository.fetchPlaces(object :
            APIResponse<List<Place>> {
            override fun onSuccess(result: List<Place>?) {
                result?.let { insertPlaces(it) }
            }

            override fun onError(t: Throwable) {
                setLoading(false)
                t.printStackTrace()
            }
        })
    }

    fun fetchCategoriesFromFirebase() = viewModelScope.launch(Dispatchers.IO) {

        setLoading(true)

        categoriesRepository.fetchCategories(object :
            APIResponse<List<Category>> {
            override fun onSuccess(result: List<Category>?) {
                result?.let { insertCategories(it) }
            }

            override fun onError(t: Throwable) {
                setLoading(false)
                t.printStackTrace()
            }
        })
    }

    fun insertPlaces(places: List<Place>) = viewModelScope.launch(Dispatchers.IO) {
        placesRepository.insertAllPlaces(places)
        setLoading(false)
    }

    fun insertCategories(categories: List<Category>) = viewModelScope.launch(Dispatchers.IO) {
        categoriesRepository.insertAllCategories(categories)
        setLoading(false)
    }

    fun updateCategory(category: Category) = viewModelScope.launch(Dispatchers.IO) {
        categoriesRepository.updateCategory(category)
    }

    fun updatePlace(place: Place) = viewModelScope.launch(Dispatchers.IO) {
        placesRepository.updatePlace(place)
    }

    fun clearDatabase() = viewModelScope.launch(Dispatchers.IO) {
        placesRepository.clearPlacesTable()
        categoriesRepository.clearCategoriesTable()
    }

    fun setLoading(isVisible: Boolean) {
        this.isLoading.postValue(isVisible)
    }
}