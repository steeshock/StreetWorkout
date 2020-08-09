package com.steeshock.android.streetworkout.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.steeshock.android.streetworkout.data.api.APIResponse
import com.steeshock.android.streetworkout.data.model.Category
import com.steeshock.android.streetworkout.data.repository.Repository
import com.steeshock.android.streetworkout.data.model.Place
import com.steeshock.android.streetworkout.data.model.State
import io.reactivex.rxjava3.disposables.CompositeDisposable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class PlacesViewModel(private val repository: Repository) : ViewModel() {

    val isLoading = MutableLiveData(false)
    private val compositeDisposable = CompositeDisposable()

    val placesLiveData = repository.allPlaces
    val categoriesLiveData = repository.allCategories

    fun updatePlaces() {
        setLoading(true)
        repository.updatePlaces(compositeDisposable, object :
            APIResponse<List<Place>> {
            override fun onSuccess(result: List<Place>?) {
                setLoading(false)
                result?.let { insertPlaces(it) }
            }

            override fun onError(t: Throwable) {
                setLoading(false)
                t.printStackTrace()
            }
        })
    }

    fun updateCategories() {
        setLoading(true)
        repository.updateCategories(compositeDisposable, object :
            APIResponse<List<Category>> {
            override fun onSuccess(result: List<Category>?) {
                setLoading(false)
                result?.let { insertCategories(it) }
            }

            override fun onError(t: Throwable) {
                setLoading(false)
                t.printStackTrace()
            }
        })
    }

    fun insertPlaces(places: List<Place>) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertAllPlaces(places)
    }

    fun insertCategories(categories: List<Category>) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertAllCategories(categories)
    }

    fun setLoading(isVisible: Boolean) {
        this.isLoading.value = isVisible
    }

    fun clearDatabase() = viewModelScope.launch(Dispatchers.IO) {
        repository.clearDatabase()
    }

    fun insertPlace(place: Place) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertPlace(place)
    }

    fun insertCategory(category: Category) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertCategory(category)
    }

    fun updateCategory(category: Category) = viewModelScope.launch(Dispatchers.IO) {
        repository.updateCategory(category)
    }

    fun updatePlace(place: Place) = viewModelScope.launch(Dispatchers.IO) {
        repository.updatePlace(place)
    }

    fun removeAllPlacesExceptFavorites(boolean: Boolean) = viewModelScope.launch(Dispatchers.IO) {
        repository.removeAllPlacesExceptFavorites(boolean)
    }
}