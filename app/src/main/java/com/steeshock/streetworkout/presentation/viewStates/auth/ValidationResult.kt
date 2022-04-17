package com.steeshock.streetworkout.presentation.viewStates.auth

enum class EmailValidationResult {
    EMPTY_EMAIL,
    NOT_VALID_EMAIL,
    SUCCESS_EMAIL_VALIDATION,
    EXISTING_EMAIL,
    INVALID_EMAIL,
}

enum class PasswordValidationResult {
    EMPTY_PASSWORD,
    NOT_VALID_PASSWORD,
    SUCCESS_PASSWORD_VALIDATION,
}