package com.steeshock.android.streetworkout.presentation.viewStates

import com.steeshock.android.streetworkout.presentation.viewmodels.ProfileViewModel.SignPurpose
import com.steeshock.android.streetworkout.presentation.viewmodels.ProfileViewModel.SignPurpose.SIGN_UP

/**
 * Describes UI state of Auth screen
 */
data class AuthViewState(

    /**
     * Show fullscreen loader
     */
    val isLoading: Boolean = false,

    /**
     * Current state of sign purpose type: Sign Up or Sign In
     */
    val signPurpose: SignPurpose = SIGN_UP,
)


