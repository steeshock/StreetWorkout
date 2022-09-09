package com.steeshock.streetworkout.presentation.viewmodels

import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
import androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode
import androidx.lifecycle.*
import com.steeshock.streetworkout.data.model.PlaceDto
import com.steeshock.streetworkout.data.repository.implementation.DataStoreRepository.PreferencesKeys.NIGHT_MODE_PREFERENCES_KEY
import com.steeshock.streetworkout.data.repository.interfaces.IDataStoreRepository
import com.steeshock.streetworkout.data.repository.interfaces.IPlacesRepository
import com.steeshock.streetworkout.domain.entity.Category
import com.steeshock.streetworkout.domain.favorites.IFavoritesInteractor
import com.steeshock.streetworkout.domain.repository.IAuthService
import com.steeshock.streetworkout.domain.repository.ICategoriesRepository
import com.steeshock.streetworkout.presentation.delegates.*
import com.steeshock.streetworkout.presentation.viewStates.EmptyViewState.*
import com.steeshock.streetworkout.presentation.viewStates.places.PlacesViewEvent
import com.steeshock.streetworkout.presentation.viewStates.places.PlacesViewEvent.*
import com.steeshock.streetworkout.presentation.viewStates.places.PlacesViewState
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
    ViewStateDelegate<PlacesViewState> by ViewStateDelegateImpl(::PlacesViewState),
    ExceptionHandler by DefaultExceptionHandler() {

    private val mediatorPlaces = MediatorLiveData<List<PlaceDto>>()
    val observablePlaces: LiveData<List<PlaceDto>> = mediatorPlaces

    val observableCategories = categoriesRepository.allCategories.asLiveData()

    private val allPlaces = placesRepository.allPlaces
    private val filteredPlaces: MutableLiveData<List<PlaceDto>> = MutableLiveData()
    private val actualPlaces: MutableLiveData<List<PlaceDto>> = MutableLiveData()

    private var filterList: MutableList<Category> = mutableListOf()
    private var lastSearchString: String? = null

    init {
        mediatorPlaces.addSource(allPlaces) {
            filteredPlaces.value = it
            filterData(filterList)
        }
        mediatorPlaces.addSource(observableCategories) {
            filterList = it.filter { category -> category.isSelected == true }.toMutableList()
            filterData(filterList)
        }
        mediatorPlaces.addSource(actualPlaces) {
            setupEmptyState()
            updatePlacesOwnerStates(it)
            mediatorPlaces.value = it.sortedByDescending { i -> i.created }
        }
        setupAppTheme()
    }

    fun fetchData() = viewModelScope.launch(Dispatchers.IO + defaultExceptionHandler {
        postViewEvent(NoInternetConnection)
        updateViewState(postValue = true) { copy(isPlacesLoading = false) }
    }) {
        coroutineScope {
            updateViewState(postValue = true) { copy(isPlacesLoading = true) }
            awaitAll(
                async { placesRepository.fetchPlaces() },
                async { categoriesRepository.fetchCategories() }
            )
            favoritesInteractor.syncFavoritePlaces()
            updateViewState(postValue = true) { copy(isPlacesLoading = false) }
        }
    }

    // TODO Temporary function for easy testing
    fun clearDatabase() = viewModelScope.launch(Dispatchers.IO) {
        placesRepository.clearPlacesTable()
        categoriesRepository.clearCategoriesTable()
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

    fun onLikeClicked(placeDto: PlaceDto) = viewModelScope.launch(Dispatchers.IO) {
        if (!authService.isUserAuthorized && !placeDto.isFavorite) {
            postViewEvent(ShowAddToFavoritesAuthAlert)
        }
        favoritesInteractor.updatePlaceFavoriteState(placeDto)
    }

    fun onPlaceDeleteClicked(placeDto: PlaceDto) = viewModelScope.launch(Dispatchers.IO) {
        if (authService.isUserAuthorized && placeDto.isUserPlaceOwner) {
            postViewEvent(ShowDeletePlaceAlert(placeDto))
        }
    }

    fun deletePlace(placeDto: PlaceDto) = viewModelScope.launch(Dispatchers.IO + defaultExceptionHandler {
        postViewEvent(NoInternetConnection)
        updateViewState(postValue = true) { copy(showFullscreenLoader = false) }
    }) {
        updateViewState(postValue = true) { copy(showFullscreenLoader = true) }
        if (placesRepository.deletePlace(placeDto)) {
            postViewEvent(ShowDeletePlaceSuccess)
        }
        updateViewState(postValue = true) { copy(showFullscreenLoader = false) }
    }

    fun onFilterByCategory(category: Category) {
        category.changeSelectedState()
        if (filterList.find { it.categoryName == category.categoryName } != null) {
            filterList.remove(category)
        } else {
            filterList.add(category)
        }
        filterData(filterList)
        updateCategory(category)
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
                it.filter { place -> place.categories?.containsAll(filterList.map { i -> i.categoryId }) == true }
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

    private fun updatePlacesOwnerStates(placeDtos: List<PlaceDto>) {
        placeDtos.forEach {
            it.isUserPlaceOwner = if (authService.isUserAuthorized) {
                it.userId == authService.currentUserId
            } else {
                false
            }
        }
    }
}
