package com.steeshock.android.streetworkout.services.auth

class AuthServiceImpl : IAuthService {
    override suspend fun isUserAuthorized(): Boolean {
        return true
    }

    override suspend fun authorize(userCredentials: AuthCredentials) {
        TODO("Not yet implemented")
    }
}