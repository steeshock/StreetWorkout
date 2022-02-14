package com.steeshock.android.streetworkout.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.steeshock.android.streetworkout.presentation.viewStates.AuthViewEvent
import com.steeshock.android.streetworkout.presentation.viewStates.AuthViewState
import com.steeshock.android.streetworkout.presentation.viewStates.SingleLiveEvent
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

    private val mutableViewEvent = SingleLiveEvent<AuthViewEvent>()
    val viewEvent get() = mutableViewEvent as LiveData<AuthViewEvent>

    fun requestAuthState() = viewModelScope.launch(Dispatchers.IO) {
        val isAuthorized = authService.isUserAuthorized()
        mutableViewState.setNewState(postValue = true) {
            copy(isUserAuthorized = isAuthorized)
        }
    }

    fun signUpNewUser(
        email: String,
        password: String,
    ) = viewModelScope.launch(Dispatchers.IO) {
        mutableViewState.setNewState(postValue = true) { copy(isLoading = true) }
        authService.signUp(
            userCredentials = UserCredentials(
                email = email,
                password = password,
            ),
            onSuccess = { email ->
                mutableViewState.setNewState(postValue = true) {
                    copy(isLoading = false)
                }
                sendViewEvent(AuthViewEvent.SuccessSignUp(userEmail = email))
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

    private fun sendViewEvent(event: AuthViewEvent) {
        mutableViewEvent.value = event
    }
}