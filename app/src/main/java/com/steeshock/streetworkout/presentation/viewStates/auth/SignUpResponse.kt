package com.steeshock.streetworkout.presentation.viewStates.auth

import com.steeshock.streetworkout.domain.entity.User

sealed class SignUpResponse {

    data class SuccessSignUp(val user: User?) : SignUpResponse()

    object UserCollisionError : SignUpResponse()

    object InvalidEmailError : SignUpResponse()
}
