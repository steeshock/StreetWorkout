package com.steeshock.streetworkout.domain.login

import com.steeshock.streetworkout.domain.repository.IUserRepository
import com.steeshock.streetworkout.domain.entity.User
import com.steeshock.streetworkout.services.auth.IAuthService
import com.steeshock.streetworkout.services.auth.UserCredentials
import javax.inject.Inject

class LoginInteractor @Inject constructor(
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