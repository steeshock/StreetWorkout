package com.steeshock.streetworkout.presentation.viewStates.addPlace

import android.location.Location

/**
 * Single view events for Add Places page
 */
sealed class AddPlaceViewEvent {
    /**
     * Success field validation before place publishing
     */
    object SuccessValidation: AddPlaceViewEvent()

    /**
     * Error while validating place title
     */
    object ErrorPlaceTitleValidation: AddPlaceViewEvent()

    /**
     * Error while validating place address
     */
    object ErrorPlaceAddressValidation: AddPlaceViewEvent()

    /**
     * Location fetching result
     */
    data class LocationResult(val location: Location?) : AddPlaceViewEvent()
}
