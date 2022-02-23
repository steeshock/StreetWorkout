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

    /**
     * Local email validation result
     */
    data class EmailValidation(val result: EmailValidationResult): AuthViewEvent()

    /**
     * Local password validation result
     */
    data class PasswordValidation(val result: PasswordValidationResult): AuthViewEvent()
}

enum class EmailValidationResult {
    EMPTY_EMAIL,
    NOT_VALID_EMAIL,
    SUCCESS_EMAIL_VALIDATION,
}

enum class PasswordValidationResult {
    EMPTY_PASSWORD,
    NOT_VALID_PASSWORD,
    SUCCESS_PASSWORD_VALIDATION,
}
