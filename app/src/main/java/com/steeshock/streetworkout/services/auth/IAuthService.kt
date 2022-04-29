package com.steeshock.streetworkout.services.auth

import com.steeshock.streetworkout.data.model.User

interface IAuthService {
    /**
     * Check if current user is authorized
     */
    val isUserAuthorized: Boolean

    /**
     * Get current authorized user email
     */
    val currentUserEmail: String

    /**
     * Get current authorized user name
     */
    val currentUserDisplayName: String

    /**
     * Get current authorized user ID
     */
    val currentUserId: String

    /**
     * [signPurpose] Sign up new user (registration)
     * or Sign in existing user (authorization)
     */
    suspend fun sign(
        userCredentials: UserCredentials,
        signPurpose: SignPurpose,
    ): User

    /**
     * Logout current authorized user
     */
    suspend fun signOut()

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
}