package com.steeshock.android.streetworkout.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.steeshock.android.streetworkout.presentation.viewStates.AuthViewState
import com.steeshock.android.streetworkout.services.auth.UserCredentials
import com.steeshock.android.streetworkout.services.auth.IAuthService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

class AuthViewModel @Inject constructor(
    private val authService: IAuthService,
) : ViewModel() {
    private val mutableViewState: MutableLiveData<AuthViewState> = MutableLiveData()
    val viewState: LiveData<AuthViewState>
        get() = mutableViewState

    fun requestAuthState() = viewModelScope.launch(Dispatchers.IO) {
        val isAuthorized = authService.isUserAuthorized()
        var userEmail: String? = null
        if (isAuthorized) {
            userEmail = authService.getUserEmail()
        }
        mutableViewState.setNewState(postValue = true) {
            copy(
                isUserAuthorized = isAuthorized,
                userEmail = userEmail,
            )
        }
    }

    fun signUpNewUser() = viewModelScope.launch(Dispatchers.IO) {
        mutableViewState.setNewState(postValue = true) { copy(isLoading = true) }
        authService.signUp(
            userCredentials = UserCredentials(
                email = "${UUID.randomUUID().toString().subSequence(0..5)}@gmail.com",
                password = "12345678",
            ),
            onSuccess = { email ->
                mutableViewState.setNewState(postValue = true) {
                    copy(
                        isLoading = false,
                        userEmail = email,
                    )
                }
            },
            onError = {
                mutableViewState.setNewState(postValue = true) { copy(isLoading = false) }
            }
        )
    }

    private fun MutableLiveData<AuthViewState>.setNewState(
        postValue: Boolean = false,
        block: AuthViewState.() -> AuthViewState,
    ) {
        val currentState = value ?: AuthViewState()
        val newState = currentState.run { block() }

        if (postValue) {
            postValue(newState)
        } else {
            value = newState
        }
    }
}