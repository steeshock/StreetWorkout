package com.steeshock.android.streetworkout.presentation.viewStates.auth

import com.steeshock.android.streetworkout.data.model.User

sealed class SignInResponse {

    data class SuccessSignIn(val user: User?) : SignInResponse()

    object UserNotAuthorized : SignInResponse()

    object InvalidUserError : SignInResponse()

    object InvalidCredentialsError : SignInResponse()
}
