package com.steeshock.android.streetworkout.presentation.viewStates.auth

import com.steeshock.android.streetworkout.data.model.User

sealed class SignUpResponse {

    data class SuccessSignUp(val user: User?) : SignUpResponse()

    object UserCollisionError : SignUpResponse()
}
