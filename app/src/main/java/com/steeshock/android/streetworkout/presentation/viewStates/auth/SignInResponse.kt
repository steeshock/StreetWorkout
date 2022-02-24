package com.steeshock.android.streetworkout.presentation.viewStates.auth

sealed class SignInResponse {

    data class SuccessSignIn(val email: String?) : SignInResponse()

    object UserNotAuthorized : SignInResponse()

    object InvalidUserError : SignInResponse()

    object InvalidCredentialsError : SignInResponse()
}
