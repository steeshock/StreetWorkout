package com.steeshock.streetworkout.presentation.viewStates.auth

import com.steeshock.streetworkout.data.model.User

sealed class SignUpResponse {

    data class SuccessSignUp(val user: User?) : SignUpResponse()

    object UserCollisionError : SignUpResponse()
}
