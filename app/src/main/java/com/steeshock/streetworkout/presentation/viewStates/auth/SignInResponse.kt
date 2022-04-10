package com.steeshock.streetworkout.presentation.viewStates.auth

import com.steeshock.streetworkout.data.model.User

sealed class SignInResponse {

    data class SuccessSignIn(val user: User?) : SignInResponse()

    object UserNotAuthorized : SignInResponse()

    object InvalidUserError : SignInResponse()

    object InvalidCredentialsError : SignInResponse()
}
