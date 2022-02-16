package com.steeshock.android.streetworkout.presentation.viewStates

/**
 * Single view events for Auth page
 */
sealed class AuthViewEvent {
    /**
     * Success registration of new user
     */
    data class SuccessSignUp(val userEmail: String?): AuthViewEvent()

    /**
     * Current user is authorized
     */
    data class SuccessSignIn(val userEmail: String?): AuthViewEvent()
}
