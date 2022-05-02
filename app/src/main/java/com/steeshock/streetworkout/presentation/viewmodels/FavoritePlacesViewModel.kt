package com.steeshock.streetworkout.presentation.viewmodels

import androidx.lifecycle.*
import com.steeshock.streetworkout.data.model.Place
import com.steeshock.streetworkout.data.repository.interfaces.IPlacesRepository
import com.steeshock.streetworkout.domain.IFavoritesInteractor
import com.steeshock.streetworkout.presentation.delegates.ViewStateDelegate
import com.steeshock.streetworkout.presentation.delegates.ViewStateDelegateImpl
import com.steeshock.streetworkout.presentation.viewStates.EmptyViewState
import com.steeshock.streetworkout.presentation.viewStates.places.PlacesViewState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.*
import javax.inject.Inject

class FavoritePlacesViewModel @Inject constructor(
    placesRepository: IPlacesRepository,
    private val favoritesInteractor: IFavoritesInteractor,
) : ViewModel(),
    ViewStateDelegate<PlacesViewState> by ViewStateDelegateImpl({ PlacesViewState() }) {

    val observablePlaces = MediatorLiveData<List<Place>>()

    private val allFavoritePlaces: LiveData<List<Place>> = placesRepository.allFavoritePlaces
    private val actualPlaces: MutableLiveData<List<Place>> = MutableLiveData()

    private var lastSearchString: String? = null

    init {
        observablePlaces.addSource(allFavoritePlaces) {
            actualPlaces.value = it
        }
        observablePlaces.addSource(actualPlaces) {
            setupEmptyState()
            observablePlaces.value = it.sortedBy { i -> i.created }
        }
    }

    fun updateFavoritePlaces() = viewModelScope.launch(Dispatchers.IO) {
        try {
            favoritesInteractor.syncFavoritePlaces(softSync = false, reloadUserData = true)
        } catch (e: Exception) {
            // TODO Handle exceptions
        }
    }

    fun onFavoriteStateChanged(place: Place) = viewModelScope.launch(Dispatchers.IO) {
        favoritesInteractor.updatePlaceFavoriteState(place)
    }

    fun returnPlaceToFavorites(place: Place) = viewModelScope.launch(Dispatchers.IO) {
        favoritesInteractor.updatePlaceFavoriteState(place, newState = true)
    }

    fun filterDataBySearchString(searchString: String?) {
        lastSearchString = searchString
        filterItemsBySearchString(lastSearchString)
    }

    private fun filterItemsBySearchString(lastSearchString: String?) {
        allFavoritePlaces.value?.let {
            actualPlaces.value = if (lastSearchString.isNullOrEmpty()){
                it
            } else {
                it.filter { place -> place.title.lowercase(Locale.ROOT).contains(lastSearchString)}
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
}