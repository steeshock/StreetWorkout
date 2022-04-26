package com.steeshock.streetworkout.presentation.viewStates.auth

import com.steeshock.streetworkout.data.model.UserInfo

sealed class SignInResponse {

    data class SuccessSignIn(val userInfo: UserInfo?) : SignInResponse()

    object UserNotAuthorized : SignInResponse()

    object InvalidUserError : SignInResponse()

    object InvalidCredentialsError : SignInResponse()
}
