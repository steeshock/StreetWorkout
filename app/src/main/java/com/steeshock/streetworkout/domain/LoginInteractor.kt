package com.steeshock.streetworkout.domain

import com.steeshock.streetworkout.data.model.User
import com.steeshock.streetworkout.data.repository.interfaces.IUserRepository
import com.steeshock.streetworkout.services.auth.IAuthService
import com.steeshock.streetworkout.services.auth.UserCredentials
import kotlinx.coroutines.delay

class LoginInteractor(
    private val authService: IAuthService,
    private val userRepository: IUserRepository,
) : ILoginInteractor {
    override suspend fun signUser(
        email: String,
        password: String,
        signPurpose: IAuthService.SignPurpose,
    ): User? {
        val userCredentials = UserCredentials(email, password)
        val signedUser = authService.sign(userCredentials, signPurpose)
        return userRepository.getOrCreateUser(signedUser)
    }
}