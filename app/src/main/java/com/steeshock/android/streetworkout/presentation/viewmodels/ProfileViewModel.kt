package com.steeshock.android.streetworkout.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.steeshock.android.streetworkout.presentation.viewStates.AuthViewEvent
import com.steeshock.android.streetworkout.presentation.viewStates.AuthViewEvent.*
import com.steeshock.android.streetworkout.presentation.viewStates.AuthViewState
import com.steeshock.android.streetworkout.presentation.viewStates.SingleLiveEvent
import com.steeshock.android.streetworkout.services.auth.IAuthService
import com.steeshock.android.streetworkout.services.auth.UserCredentials
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class ProfileViewModel @Inject constructor(
    private val authService: IAuthService,
) : ViewModel() {
    private val mutableViewState: MutableLiveData<AuthViewState> = MutableLiveData()
    val viewState: LiveData<AuthViewState>
        get() = mutableViewState

    private val mutableViewEvent = SingleLiveEvent<AuthViewEvent>()
    val viewEvent get() = mutableViewEvent as LiveData<AuthViewEvent>

    fun requestAuthState() = viewModelScope.launch(Dispatchers.IO) {
        if (authService.isUserAuthorized()) {
            sendViewEvent(
                postValue = true,
                event = SuccessSignIn(authService.getUserEmail()),
            )
        }
    }

    fun signInUser(
        email: String,
        password: String,
    ) = viewModelScope.launch(Dispatchers.IO) {
        mutableViewState.updateState(postValue = true) { copy(isLoading = true) }
        authService.signIn(
            userCredentials = UserCredentials(
                email = email,
                password = password,
            ),
            onSuccess = { email ->
                mutableViewState.updateState(postValue = true) {
                    copy(isLoading = false)
                }
                sendViewEvent(SuccessSignIn(userEmail = email))
            },
            onError = {
                mutableViewState.updateState(postValue = true) { copy(isLoading = false) }
            }
        )
    }

    fun signUpNewUser(
        email: String,
        password: String,
    ) = viewModelScope.launch(Dispatchers.IO) {
        mutableViewState.updateState(postValue = true) { copy(isLoading = true) }
        authService.signUp(
            userCredentials = UserCredentials(
                email = email,
                password = password,
            ),
            onSuccess = { email ->
                mutableViewState.updateState(postValue = true) {
                    copy(isLoading = false)
                }
                sendViewEvent(SuccessSignUp(userEmail = email))
            },
            onError = {
                mutableViewState.updateState(postValue = true) { copy(isLoading = false) }
            }
        )
    }

    private fun MutableLiveData<AuthViewState>.updateState(
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