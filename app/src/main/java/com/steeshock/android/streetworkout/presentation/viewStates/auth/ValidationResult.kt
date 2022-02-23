package com.steeshock.android.streetworkout.presentation.viewStates.auth

enum class EmailValidationResult {

    // Local validation
    EMPTY_EMAIL,
    NOT_VALID_EMAIL,
    SUCCESS_EMAIL_VALIDATION,

    // Server validation
    EXISTING_EMAIL,
    INVALID_EMAIL,
}

enum class PasswordValidationResult {

    // Local validation
    EMPTY_PASSWORD,
    NOT_VALID_PASSWORD,
    SUCCESS_PASSWORD_VALIDATION,
}