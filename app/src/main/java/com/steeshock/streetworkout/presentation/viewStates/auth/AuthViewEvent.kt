package com.steeshock.streetworkout.presentation.viewStates.auth

/**
 * Single view events for Auth page
 */
sealed class AuthViewEvent {
    /**
     * Result of new user registration request
     */
    data class SignUpResult(val result: SignUpResponse) : AuthViewEvent()

    /**
     * Result of user authorization request
     */
    data class SignInResult(val result: SignInResponse) : AuthViewEvent()

    /**
     * Unknown error while Sign Up/Sign In
     */
    object UnknownError : AuthViewEvent()

    /**
     * Local email validation result
     */
    data class EmailValidation(val result: EmailValidationResult) : AuthViewEvent()

    /**
     * Local password validation result
     */
    data class PasswordValidation(val result: PasswordValidationResult) : AuthViewEvent()

    /**
     * User success logout (SignOut)
     */
    object SignOut : AuthViewEvent()
}
