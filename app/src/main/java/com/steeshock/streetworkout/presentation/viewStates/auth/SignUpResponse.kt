package com.steeshock.streetworkout.presentation.viewStates.auth

import com.steeshock.streetworkout.data.model.UserInfo

sealed class SignUpResponse {

    data class SuccessSignUp(val userInfo: UserInfo?) : SignUpResponse()

    object UserCollisionError : SignUpResponse()

    object InvalidEmailError : SignUpResponse()
}
