package com.steeshock.streetworkout.interactor

import com.steeshock.streetworkout.interactor.repository.IUserRepository
import com.steeshock.streetworkout.interactor.entity.User
import com.steeshock.streetworkout.interactor.entity.UserCredentials
import com.steeshock.streetworkout.interactor.entity.enums.SignPurpose
import com.steeshock.streetworkout.interactor.interactor.ILoginInteractor
import com.steeshock.streetworkout.interactor.repository.IAuthService
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