package com.steeshock.android.streetworkout.presentation.viewStates

/**
 * Single view events for Places page
 */
sealed class PlacesViewEvent {
    /**
     * Open page for adding new place
     */
    object ShowAddPlaceFragment: PlacesViewEvent()

    /**
     * Show alert for non-authorized users
     */
    object ShowAuthenticationAlert: PlacesViewEvent()
}
