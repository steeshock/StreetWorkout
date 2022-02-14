package com.steeshock.android.streetworkout.services.auth

interface IAuthService {
    /**
     * Check if current user is authorized
     */
    suspend fun isUserAuthorized(): Boolean

    /**
     * Authorization with credentials
     */
    suspend fun authorize(userCredentials: AuthCredentials)
}