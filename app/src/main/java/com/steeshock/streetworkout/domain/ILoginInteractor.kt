package com.steeshock.streetworkout.domain

import com.steeshock.streetworkout.data.model.User
import com.steeshock.streetworkout.services.auth.IAuthService

interface ILoginInteractor {

    /**
     * After success sign up/sign in with Firebase auth, get (if exists)
     * or create additional User instance in remote storage and return it
     */
    suspend fun signUser(
        email: String,
        password: String,
        signPurpose: IAuthService.SignPurpose,
    ): User?
}