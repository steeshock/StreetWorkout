package com.steeshock.streetworkout.presentation.viewStates.auth

import com.steeshock.streetworkout.domain.entity.enums.SignPurpose
import com.steeshock.streetworkout.domain.entity.enums.SignPurpose.SIGN_UP

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


