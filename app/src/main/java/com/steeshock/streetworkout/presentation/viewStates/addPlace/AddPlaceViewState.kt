package com.steeshock.streetworkout.presentation.viewStates.addPlace

import android.location.Location


/**
 * Describes UI state of Add Place screen
 */
data class AddPlaceViewState(

    val loadCompleted: Boolean = false,

    var isImagePickingInProgress: Boolean = false,

    var isLocationInProgress: Boolean = false,

    var isSendingInProgress: Boolean = false,

    var selectedImagesCount: Int = 0,

    var selectedCategories: String = "",

    var sendingProgress: Int = 0,

    var maxProgressValue: Int = 0,

    var placeAddress: String? = "",

    var placeLocation: Location? = null,
)