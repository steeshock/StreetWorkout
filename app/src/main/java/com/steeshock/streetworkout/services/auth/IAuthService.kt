package com.steeshock.streetworkout.services.auth

import com.steeshock.streetworkout.data.model.User

interface IAuthService {
    /**
     * Check if current user is authorized
     */
    suspend fun isUserAuthorized(): Boolean

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
     * Sign up new user (registration)
     */
    suspend fun signUp(
        userCredentials: UserCredentials,
        onSuccess: (User) -> Unit,
        onError: (Exception) -> Unit,
    )

    /**
     * Sign in existing user (authorization)
     */
    suspend fun signIn(
        userCredentials: UserCredentials,
        onSuccess: (User) -> Unit,
        onError: (Exception) -> Unit,
    )

    /**
     * Logout current authorized user
     */
    suspend fun signOut()
}