package com.steeshock.android.streetworkout.services.auth

import com.steeshock.android.streetworkout.data.model.User

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
     * Get current authorized user name
     */
    suspend fun getDisplayName(): String?

    /**
     * Get current authorized user ID
     */
    fun getCurrentUserId(): String?

    /**
     * Sign up new user (registration)
     */
    suspend fun signUp(
        userCredentials: UserCredentials,
        onSuccess: (User?) -> Unit,
        onError: (Exception) -> Unit,
    )

    /**
     * Sign in existing user (authorization)
     */
    suspend fun signIn(
        userCredentials: UserCredentials,
        onSuccess: (User?) -> Unit,
        onError: (Exception) -> Unit,
    )

    /**
     * Logout current authorized user
     */
    suspend fun signOut()
}