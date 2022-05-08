package com.steeshock.streetworkout.presentation.viewmodels

import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
import androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.steeshock.streetworkout.data.model.Category
import com.steeshock.streetworkout.data.model.Place
import com.steeshock.streetworkout.data.repository.implementation.DataStoreRepository.PreferencesKeys.NIGHT_MODE_PREFERENCES_KEY
import com.steeshock.streetworkout.data.repository.interfaces.ICategoriesRepository
import com.steeshock.streetworkout.data.repository.interfaces.IDataStoreRepository
import com.steeshock.streetworkout.data.repository.interfaces.IPlacesRepository
import com.steeshock.streetworkout.domain.favorites.IFavoritesInteractor
import com.steeshock.streetworkout.presentation.delegates.ViewEventDelegate
import com.steeshock.streetworkout.presentation.delegates.ViewEventDelegateImpl
import com.steeshock.streetworkout.presentation.delegates.ViewStateDelegate
import com.steeshock.streetworkout.presentation.delegates.ViewStateDelegateImpl
import com.steeshock.streetworkout.presentation.viewStates.EmptyViewState.*
import com.steeshock.streetworkout.presentation.viewStates.places.PlacesViewEvent
import com.steeshock.streetworkout.presentation.viewStates.places.PlacesViewEvent.*
import com.steeshock.streetworkout.presentation.viewStates.places.PlacesViewState
import com.steeshock.streetworkout.services.auth.IAuthService
import kotlinx.coroutines.*
import java.util.*
import javax.inject.Inject

class PlacesViewModel @Inject constructor(
    private val placesRepository: IPlacesRepository,
    private val categoriesRepository: ICategoriesRepository,
    private val authService: IAuthService,
    private val dataStoreRepository: IDataStoreRepository,
    private val favoritesInteractor: IFavoritesInteractor,
) : ViewModel(),
    ViewEventDelegate<PlacesViewEvent> by ViewEventDelegateImpl(),
    ViewStateDelegate<PlacesViewState> by ViewStateDelegateImpl({ PlacesViewState() }) {

    val observablePlaces = MediatorLiveData<List<Place>>()
    val observableCategories = categoriesRepository.allCategories

    private val allPlaces = placesRepository.allPlaces
    private val filteredPlaces: MutableLiveData<List<Place>> = MutableLiveData()
    private val actualPlaces: MutableLiveData<List<Place>> = MutableLiveData()

    private var filterList: MutableList<Category> = mutableListOf()
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
            observablePlaces.value = it.sortedByDescending { i -> i.created }
        }
        setupAppTheme()
    }

    fun fetchData() = viewModelScope.launch(Dispatchers.IO) {
        updateViewState(postValue = true) { copy(isLoading = true) }
        try {
            coroutineScope {
                awaitAll(
                    async { placesRepository.fetchPlaces() },
                    async { categoriesRepository.fetchCategories() }
                )
                favoritesInteractor.syncFavoritePlaces()
            }
        } catch (e: Exception) {
            handleError(e)
        } finally {
            updateViewState(postValue = true) { copy(isLoading = false) }
        }
    }

    // TODO Temporary function for easy testing
    fun clearDatabase() = viewModelScope.launch(Dispatchers.IO) {
        placesRepository.clearPlacesTable()
        categoriesRepository.clearCategoriesTable()
    }

    fun onLikeClicked(place: Place) = viewModelScope.launch(Dispatchers.IO) {
        if (!authService.isUserAuthorized && !place.isFavorite) {
            postViewEvent(ShowAddToFavoritesAuthAlert)
        }
        favoritesInteractor.updatePlaceFavoriteState(place)
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

    fun onAddNewPlaceClicked() = viewModelScope.launch(Dispatchers.IO) {
        when {
            authService.isUserAuthorized -> {
                postViewEvent(ShowAddPlaceFragment)
            }
            else -> {
                postViewEvent(ShowAddPlaceAuthAlert)
            }
        }
    }

    fun resetSearchFilter() {
        if (!lastSearchString.isNullOrEmpty()) {
            filterDataBySearchString(null)
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
            filteredPlaces.value?.filter {
                it.title.lowercase(Locale.ROOT).contains(lastSearchString.lowercase())
            }
        }
    }

    private fun filterData(filterList: MutableList<Category>) {
        allPlaces.value?.let {
            actualPlaces.value = if (filterList.isEmpty()) {
                it
            } else {
                it.filter { place -> place.categories?.containsAll(filterList.map { i -> i.category_id }) == true }
            }

            filteredPlaces.value = actualPlaces.value
        }

        if (!lastSearchString.isNullOrEmpty()) {
            filterDataBySearchString(lastSearchString)
        }
    }

    private fun setupAppTheme() = viewModelScope.launch(Dispatchers.IO) {
        dataStoreRepository.getInt(NIGHT_MODE_PREFERENCES_KEY)?.let {
            withContext(Dispatchers.Main) {
                if (it != MODE_NIGHT_FOLLOW_SYSTEM) {
                    setDefaultNightMode(it)
                }
            }
        }
    }

    private fun setupEmptyState() {
        when {
            allPlaces.value.isNullOrEmpty() -> {
                updateViewState {
                    copy(emptyState = EMPTY_PLACES)
                }
            }
            actualPlaces.value.isNullOrEmpty() -> {
                updateViewState {
                    copy(emptyState = EMPTY_SEARCH_RESULTS)
                }
            }
            else -> {
                updateViewState {
                    copy(emptyState = NOT_EMPTY)
                }
            }
        }
    }

    private fun updateCategory(category: Category) = viewModelScope.launch(Dispatchers.IO) {
        categoriesRepository.updateCategory(category)
    }

    // TODO Handle errors on UI
    private fun handleError(exception: Exception) {
        updateViewState(postValue = true) {
            copy(isLoading = false)
        }
    }
}
