package com.steeshock.android.streetworkout.presentation.viewStates.auth

sealed class SignUpResponse {

    data class SuccessSignUp(val email: String?) : SignUpResponse()

    object UserCollisionError : SignUpResponse()
}
