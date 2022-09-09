package com.steeshock.streetworkout.presentation.viewStates.places

import com.steeshock.streetworkout.interactor.entity.Place

/**
 * Single view events for Places page
 */
sealed class PlacesViewEvent {
    /**
     * Open page for adding new place
     */
    object ShowAddPlaceFragment : PlacesViewEvent()

    /**
     * Show alert when click to add place for non-authorized users
     */
    object ShowAddPlaceAuthAlert : PlacesViewEvent()

    /**
     * Show alert when add to favorites for non-authorized users
     */
    object ShowAddToFavoritesAuthAlert : PlacesViewEvent()

    /**
     * Show alert when user try to delete place
     */
    data class ShowDeletePlaceAlert(val place: Place) : PlacesViewEvent()

    /**
     * Show snackbar if place deleted successfully
     */
    object ShowDeletePlaceSuccess : PlacesViewEvent()

    /**
     * Show notification when there is no active internet connection
     */
    object NoInternetConnection : PlacesViewEvent()
}
