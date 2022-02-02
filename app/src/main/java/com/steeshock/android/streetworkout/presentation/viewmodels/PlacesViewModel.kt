package com.steeshock.android.streetworkout.presentation.viewmodels

import androidx.lifecycle.*
import com.steeshock.android.streetworkout.data.api.APIResponse
import com.steeshock.android.streetworkout.data.model.Category
import com.steeshock.android.streetworkout.data.model.Place
import com.steeshock.android.streetworkout.data.repository.interfaces.ICategoriesRepository
import com.steeshock.android.streetworkout.data.repository.interfaces.IPlacesRepository
import com.steeshock.android.streetworkout.presentation.viewStates.PlacesViewState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

class PlacesViewModel @Inject constructor(
    private val placesRepository: IPlacesRepository,
    private val categoriesRepository: ICategoriesRepository,
) : ViewModel() {

    private val mutableViewState: MutableLiveData<PlacesViewState> = MutableLiveData()
    val viewState: LiveData<PlacesViewState>
        get() = mutableViewState

    val observablePlaces = MediatorLiveData<List<Place>>()
    val observableCategories = categoriesRepository.allCategories

    private val allPlaces = placesRepository.allPlaces
    private val filteredPlaces: MutableLiveData<List<Place>> = MutableLiveData()
    private val actualPlaces: MutableLiveData<List<Place>> = MutableLiveData()

    private var lastSearchString: String? = null

    init {
        observablePlaces.addSource(allPlaces) {
            filteredPlaces.value = it
            filterData(filterList)
        }
        observablePlaces.addSource(observableCategories) {
            filterList = it.filter { category -> category.isSelected == true }.toMutableList()
            filterData(filterList)
        }
        observablePlaces.addSource(actualPlaces) {
            observablePlaces.value = it.sortedBy { i -> i.created }
        }
    }
    private var filterList: MutableList<Category> = mutableListOf()

    fun fetchPlaces() {
        mutableViewState.setNewState { copy(isLoading = true) }
        viewModelScope.launch(Dispatchers.IO) {
            placesRepository.fetchPlaces(object :
                APIResponse<List<Place>> {
                override fun onSuccess(result: List<Place>?) {
                    mutableViewState.setNewState { copy(isLoading = false) }
                    result?.let { insertPlaces(it) }
                }

                override fun onError(t: Throwable) {
                    mutableViewState.setNewState { copy(isLoading = false) }
                    t.printStackTrace()
                }
            })
        }
    }

    fun fetchCategories() {
        mutableViewState.setNewState { copy(isLoading = true) }
        viewModelScope.launch(Dispatchers.IO) {
            categoriesRepository.fetchCategories(object :
                APIResponse<List<Category>> {
                override fun onSuccess(result: List<Category>?) {
                    mutableViewState.setNewState {copy(isLoading = false) }
                    result?.let { insertCategories(it) }
                }

                override fun onError(t: Throwable) {
                    mutableViewState.setNewState { copy(isLoading = false) }
                    t.printStackTrace()
                }
            })
        }
    }

    fun insertPlaces(places: List<Place>) = viewModelScope.launch(Dispatchers.IO) {
        placesRepository.insertAllPlaces(places)
    }

    fun insertCategories(categories: List<Category>) = viewModelScope.launch(Dispatchers.IO) {
        categoriesRepository.insertAllCategories(categories)
    }

    fun updateCategory(category: Category) = viewModelScope.launch(Dispatchers.IO) {
        categoriesRepository.updateCategory(category)
    }

    private fun updatePlace(place: Place) = viewModelScope.launch(Dispatchers.IO) {
        placesRepository.updatePlace(place)
    }

    fun clearDatabase() = viewModelScope.launch(Dispatchers.IO) {
        placesRepository.clearPlacesTable()
        categoriesRepository.clearCategoriesTable()
    }

    private fun MutableLiveData<PlacesViewState>.setNewState(
        block: PlacesViewState.() -> PlacesViewState,
    ) {
        val currentState = value ?: PlacesViewState()
        val newState = currentState.run { block() }
        value = newState
    }

    fun onLikeClicked(place: Place) {
        place.changeFavoriteState()
        updatePlace(place)
    }

    fun onFilterByCategory(category: Category) {
        category.changeSelectedState()
        if (filterList.find { it.category_name == category.category_name } != null) {
            filterList.remove(category)
        } else {
            filterList.add(category)
        }
        filterData(filterList)
        updateCategory(category)
    }

    private fun filterData(filterList: MutableList<Category>) {
        allPlaces.value?.let {
            actualPlaces.value = if (filterList.isEmpty())
                it
            else {
                it.filter { place -> place.categories!!.containsAll(filterList.map { i -> i.category_id })}
            }

            filteredPlaces.value = actualPlaces.value
        }

        if (!lastSearchString.isNullOrEmpty()) {
            filterDataBySearchString(lastSearchString)
        }
    }

    fun filterDataBySearchString(searchString: String?) {
        lastSearchString = searchString
        filterItemsBySearchString(lastSearchString)
    }

    private fun filterItemsBySearchString(lastSearchString: String?) {
        actualPlaces.value = if (lastSearchString.isNullOrEmpty())
            filteredPlaces.value
        else {
            filteredPlaces.value?.filter { it.title.lowercase(Locale.ROOT).contains(lastSearchString)}
        }
    }
}