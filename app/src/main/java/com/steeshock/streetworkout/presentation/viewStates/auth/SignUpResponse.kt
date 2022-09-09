package com.steeshock.streetworkout.presentation.viewStates.auth

import com.steeshock.streetworkout.interactor.entity.User

sealed class SignUpResponse {

    data class SuccessSignUp(val user: User?) : SignUpResponse()

    object UserCollisionError : SignUpResponse()

    object InvalidEmailError : SignUpResponse()
}
