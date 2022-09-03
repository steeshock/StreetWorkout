package com.steeshock.streetworkout.domain.login

import com.steeshock.streetworkout.data.model.UserDto
import com.steeshock.streetworkout.domain.entity.User
import com.steeshock.streetworkout.services.auth.IAuthService

/**
 * Interactor to signIn/signUp user with Auth service
 * and get or create instance os User both remote and locally
 */
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