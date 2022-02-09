package com.steeshock.android.streetworkout.presentation.viewmodels

import androidx.lifecycle.*
import com.steeshock.android.streetworkout.data.api.APIResponse
import com.steeshock.android.streetworkout.data.model.Category
import com.steeshock.android.streetworkout.data.model.Place
import com.steeshock.android.streetworkout.data.repository.interfaces.ICategoriesRepository
import com.steeshock.android.streetworkout.data.repository.interfaces.IPlacesRepository
import com.steeshock.android.streetworkout.presentation.viewStates.EmptyViewState.*
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
            setupEmptyState()
            observablePlaces.value = it.sortedBy { i -> i.created }
        }
    }

    private fun setupEmptyState() {
        when {
            allPlaces.value.isNullOrEmpty() -> {
                mutableViewState.setNewState {
                    copy(emptyState = EMPTY_PLACES)
                }
            }
            actualPlaces.value.isNullOrEmpty() -> {
                mutableViewState.setNewState {
                    copy(emptyState = EMPTY_SEARCH_RESULTS)
                }
            }
            else -> {
                mutableViewState.setNewState {
                    copy(emptyState = NOT_EMPTY)
                }
            }
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

    private fun updateCategory(category: Category) = viewModelScope.launch(Dispatchers.IO) {
        categoriesRepository.updateCategory(category)
    }

    private fun updatePlace(place: Place) = viewModelScope.launch(Dispatchers.IO) {
        placesRepository.updatePlace(place)
    }

    fun clearDatabase() = viewModelScope.launch(Dispatchers.IO) {
        placesRepository.clearPlacesTable()
        categoriesRepository.clearCategoriesTable()
    }

    fun onLikeClicked(place: Place) {
        updatePlace(place.copy(isFavorite = !place.isFavorite))
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
            actualPlaces.value = if (filterList.isEmpty()) {
                it
            } else {
                it.filter { place -> place.categories?.containsAll(filterList.map { i -> i.category_id }) == true}
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

    private fun MutableLiveData<PlacesViewState>.setNewState(
        block: PlacesViewState.() -> PlacesViewState,
    ) {
        val currentState = value ?: PlacesViewState()
        val newState = currentState.run { block() }
        value = newState
    }
}