package com.steeshock.android.streetworkout.presentation.viewmodels

import androidx.lifecycle.*
import com.steeshock.android.streetworkout.data.model.Place
import com.steeshock.android.streetworkout.data.repository.interfaces.IPlacesRepository
import com.steeshock.android.streetworkout.presentation.viewStates.EmptyViewState
import com.steeshock.android.streetworkout.presentation.viewStates.PlacesViewState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

class FavoritePlacesViewModel @Inject constructor(
    private val placesRepository: IPlacesRepository,
) : ViewModel() {

    private val mutableViewState: MutableLiveData<PlacesViewState> = MutableLiveData()
    val viewState: LiveData<PlacesViewState>
        get() = mutableViewState

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

    private fun setupEmptyState() {
        when {
            allFavoritePlaces.value.isNullOrEmpty() -> {
                mutableViewState.setNewState {
                    copy(emptyState = EmptyViewState.EMPTY_PLACES)
                }
            }
            actualPlaces.value.isNullOrEmpty() -> {
                mutableViewState.setNewState {
                    copy(emptyState = EmptyViewState.EMPTY_SEARCH_RESULTS)
                }
            }
            else -> {
                mutableViewState.setNewState {
                    copy(emptyState = EmptyViewState.NOT_EMPTY)
                }
            }
        }
    }

    fun onFavoriteStateChanged(place: Place) = viewModelScope.launch(Dispatchers.IO) {
        place.changeFavoriteState()
        insertPlace(place)
    }

    fun returnPlaceToFavorites(place: Place) = viewModelScope.launch(Dispatchers.IO) {
        place.changeFavoriteState()
        insertPlace(place)
    }

    private fun insertPlace(place: Place) = viewModelScope.launch(Dispatchers.IO) {
        placesRepository.insertPlaceLocal(place)
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

    private fun MutableLiveData<PlacesViewState>.setNewState(
        block: PlacesViewState.() -> PlacesViewState,
    ) {
        val currentState = value ?: PlacesViewState()
        val newState = currentState.run { block() }
        value = newState
    }
}