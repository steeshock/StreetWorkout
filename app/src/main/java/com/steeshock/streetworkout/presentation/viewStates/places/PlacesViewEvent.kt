package com.steeshock.streetworkout.presentation.viewStates.places

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
     * Show notification when there is no active internet connection
     */
    object NoInternetConnection : PlacesViewEvent()
}
