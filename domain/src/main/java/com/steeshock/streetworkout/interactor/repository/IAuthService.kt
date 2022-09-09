package com.steeshock.streetworkout.domain.repository

import com.steeshock.streetworkout.domain.entity.User
import com.steeshock.streetworkout.domain.entity.UserCredentials
import com.steeshock.streetworkout.domain.entity.enums.SignPurpose

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
}