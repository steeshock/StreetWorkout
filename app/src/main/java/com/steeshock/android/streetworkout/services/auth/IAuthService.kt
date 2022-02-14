package com.steeshock.android.streetworkout.services.auth

interface IAuthService {
    /**
     * Check if current user is authorized
     */
    suspend fun isUserAuthorized(): Boolean

    /**
     * Get current authorized user email
     */
    suspend fun getUserEmail(): String?

    /**
     * Sign up new user (registration)
     */
    suspend fun signUp(
        userCredentials: UserCredentials,
        onSuccess: (String?) -> Unit,
        onError: () -> Unit,
    )

    /**
     * Sign in existing user (authorization)
     */
    suspend fun signIn(
        userCredentials: UserCredentials,
        onSuccess: () -> Unit,
        onError: () -> Unit,
    )
}