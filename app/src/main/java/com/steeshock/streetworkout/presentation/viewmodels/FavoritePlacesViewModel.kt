package com.steeshock.streetworkout.presentation.viewmodels

import androidx.lifecycle.*
import com.steeshock.streetworkout.data.model.PlaceDto
import com.steeshock.streetworkout.data.repository.interfaces.IPlacesRepository
import com.steeshock.streetworkout.domain.favorites.IFavoritesInteractor
import com.steeshock.streetworkout.presentation.delegates.*
import com.steeshock.streetworkout.presentation.viewStates.EmptyViewState
import com.steeshock.streetworkout.presentation.viewStates.places.PlacesViewEvent
import com.steeshock.streetworkout.presentation.viewStates.places.PlacesViewEvent.NoInternetConnection
import com.steeshock.streetworkout.presentation.viewStates.places.PlacesViewState
import com.steeshock.streetworkout.domain.repository.IAuthService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

class FavoritePlacesViewModel @Inject constructor(
    private val placesRepository: IPlacesRepository,
    private val favoritesInteractor: IFavoritesInteractor,
    private val authService: IAuthService,
) : ViewModel(),
    ViewEventDelegate<PlacesViewEvent> by ViewEventDelegateImpl(),
    ViewStateDelegate<PlacesViewState> by ViewStateDelegateImpl(::PlacesViewState),
    ExceptionHandler by DefaultExceptionHandler() {

    private val mediatorPlaces = MediatorLiveData<List<PlaceDto>>()
    val observablePlaces: LiveData<List<PlaceDto>> = mediatorPlaces

    private val allFavoritePlaces: LiveData<List<PlaceDto>> = placesRepository.allFavoritePlaces
    private val actualPlaces: MutableLiveData<List<PlaceDto>> = MutableLiveData()

    private var lastSearchString: String? = null

    init {
        mediatorPlaces.addSource(allFavoritePlaces) {
            actualPlaces.value = it
            filterData()
        }
        mediatorPlaces.addSource(actualPlaces) {
            setupEmptyState()
            updatePlacesOwnerStates(it)
            mediatorPlaces.value = it.sortedByDescending { i -> i.created }
        }
    }

    fun updateFavoritePlaces() = viewModelScope.launch(Dispatchers.IO + defaultExceptionHandler {
        postViewEvent(NoInternetConnection)
        updateViewState(postValue = true) { copy(isPlacesLoading = false) }
    }) {
        coroutineScope {
            updateViewState(postValue = true) { copy(isPlacesLoading = true) }
            favoritesInteractor.syncFavoritePlaces(softSync = false, reloadUserData = true)
            updateViewState(postValue = true) { copy(isPlacesLoading = false) }
        }
    }

    fun onFavoriteStateChanged(placeDto: PlaceDto) = viewModelScope.launch(Dispatchers.IO) {
        favoritesInteractor.updatePlaceFavoriteState(placeDto)
    }

    fun returnPlaceToFavorites(placeDto: PlaceDto) = viewModelScope.launch(Dispatchers.IO) {
        favoritesInteractor.updatePlaceFavoriteState(placeDto, newState = true)
    }

    fun filterDataBySearchString(searchString: String?) {
        lastSearchString = searchString
        filterItemsBySearchString(lastSearchString)
    }

    fun resetSearchFilter() {
        if (!lastSearchString.isNullOrEmpty()) {
            filterDataBySearchString(null)
        }
    }

    fun onPlaceDeleteClicked(placeDto: PlaceDto) = viewModelScope.launch(Dispatchers.IO) {
        if (authService.isUserAuthorized && placeDto.isUserPlaceOwner) {
            postViewEvent(PlacesViewEvent.ShowDeletePlaceAlert(placeDto))
        }
    }

    fun deletePlace(placeDto: PlaceDto) = viewModelScope.launch(Dispatchers.IO + defaultExceptionHandler {
        postViewEvent(NoInternetConnection)
        updateViewState(postValue = true) { copy(showFullscreenLoader = false) }
    }) {
        updateViewState(postValue = true) { copy(showFullscreenLoader = true) }
        if (placesRepository.deletePlace(placeDto)) {
            postViewEvent(PlacesViewEvent.ShowDeletePlaceSuccess)
        }
        updateViewState(postValue = true) { copy(showFullscreenLoader = false) }
    }

    private fun filterData() {
        if (!lastSearchString.isNullOrEmpty()) {
            filterDataBySearchString(lastSearchString)
        }
    }

    private fun filterItemsBySearchString(lastSearchString: String?) {
        allFavoritePlaces.value?.let {
            actualPlaces.value = if (lastSearchString.isNullOrEmpty()) {
                it
            } else {
                it.filter { place -> place.title.lowercase(Locale.ROOT).contains(lastSearchString) }
            }
        }
    }

    private fun setupEmptyState() {
        when {
            allFavoritePlaces.value.isNullOrEmpty() -> {
                updateViewState {
                    copy(emptyState = EmptyViewState.EMPTY_PLACES)
                }
            }
            actualPlaces.value.isNullOrEmpty() -> {
                updateViewState {
                    copy(emptyState = EmptyViewState.EMPTY_SEARCH_RESULTS)
                }
            }
            else -> {
                updateViewState {
                    copy(emptyState = EmptyViewState.NOT_EMPTY)
                }
            }
        }
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