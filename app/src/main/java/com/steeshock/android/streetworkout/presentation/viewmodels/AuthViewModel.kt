package com.steeshock.android.streetworkout.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.steeshock.android.streetworkout.presentation.viewStates.AuthViewState
import com.steeshock.android.streetworkout.presentation.viewStates.PlacesViewState
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

    fun onRequestAuthState() = viewModelScope.launch(Dispatchers.IO) {
        val isAuthorized = authService.isUserAuthorized()
        mutableViewState.setNewState(postValue = true) {
            copy(isUserAuthorized = isAuthorized)
        }
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