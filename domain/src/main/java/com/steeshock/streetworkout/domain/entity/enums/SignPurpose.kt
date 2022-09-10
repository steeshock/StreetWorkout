package com.steeshock.streetworkout.domain.entity.enums

/**
 * For security reasons, we should not check
 * the password length while user Sing In
 *
 * That's why we should separate validation for Sign Up and Sing In
 */
enum class SignPurpose {
    SIGN_UP,
    SIGN_IN;

    companion object {
        fun fromString(value: String?): SignPurpose {
            return values().firstOrNull { it.name == value } ?: SIGN_UP
        }
    }
}