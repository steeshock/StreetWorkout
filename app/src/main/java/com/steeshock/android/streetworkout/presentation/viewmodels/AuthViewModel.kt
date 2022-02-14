package com.steeshock.android.streetworkout.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.steeshock.android.streetworkout.presentation.viewStates.AuthViewEvent
import com.steeshock.android.streetworkout.presentation.viewStates.AuthViewEvent.*
import com.steeshock.android.streetworkout.presentation.viewStates.AuthViewState
import com.steeshock.android.streetworkout.presentation.viewStates.SingleLiveEvent
import com.steeshock.android.streetworkout.services.auth.UserCredentials
import com.steeshock.android.streetworkout.services.auth.IAuthService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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
        if (authService.isUserAuthorized()) {
            mutableViewState.setNewState(postValue = true) { copy(isLoading = true) }

            // TODO("Delay for more smooth UX")
            delay(1500)

            sendViewEvent(
                postValue = true,
                event = SuccessAuthorization,
            )
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
                sendViewEvent(SuccessSignUp(userEmail = email))
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

    private fun sendViewEvent(
        event: AuthViewEvent,
        postValue: Boolean = false,
    ) {
        if (postValue) {
            mutableViewEvent.postValue(event)
        } else {
            mutableViewEvent.value = event
        }
    }
}