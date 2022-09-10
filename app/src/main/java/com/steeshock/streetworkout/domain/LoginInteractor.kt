package com.steeshock.streetworkout.domain

import com.steeshock.streetworkout.domain.repository.IUserRepository
import com.steeshock.streetworkout.domain.entity.User
import com.steeshock.streetworkout.domain.entity.UserCredentials
import com.steeshock.streetworkout.domain.entity.enums.SignPurpose
import com.steeshock.streetworkout.domain.interactor.ILoginInteractor
import com.steeshock.streetworkout.domain.repository.IAuthService
import javax.inject.Inject

class LoginInteractor @Inject constructor(
    private val authService: IAuthService,
    private val userRepository: IUserRepository,
) : ILoginInteractor {
    override suspend fun signUser(
        email: String,
        password: String,
        signPurpose: SignPurpose,
    ): User? {
        val userCredentials = UserCredentials(email, password)
        val signedUser = authService.sign(userCredentials, signPurpose)
        return userRepository.getOrCreateUser(signedUser)
    }
}