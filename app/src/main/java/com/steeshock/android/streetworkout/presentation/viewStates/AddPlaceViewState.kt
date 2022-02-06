package com.steeshock.android.streetworkout.presentation.viewStates

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.MutableLiveData

/**
 * Describes UI state of Add Place screen
 */
data class AddPlaceViewState(

    val loadCompleted: Boolean = false,

    var isImagePickingInProgress: Boolean = false,

    var isLocationInProgress: Boolean = false,

    var isSendingInProgress: Boolean = false,

    var selectedImagesMessage: String = "",

    var sendingProgress: Int = 0,

    var maxProgressValue: Int = 0,
)