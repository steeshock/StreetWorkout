package com.steeshock.android.streetworkout.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.steeshock.android.streetworkout.presentation.viewStates.*
import com.steeshock.android.streetworkout.presentation.viewStates.AuthViewEvent.*
import com.steeshock.android.streetworkout.presentation.viewStates.EmailValidationResult.*
import com.steeshock.android.streetworkout.presentation.viewStates.PasswordValidationResult.*
import com.steeshock.android.streetworkout.presentation.viewmodels.ProfileViewModel.ValidationPurpose.*
import com.steeshock.android.streetworkout.services.auth.IAuthService
import com.steeshock.android.streetworkout.services.auth.UserCredentials
import com.steeshock.android.streetworkout.utils.extensions.isEmailValid
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class ProfileViewModel @Inject constructor(
    private val authService: IAuthService,
) : ViewModel() {

    private companion object {
        private const val MIN_PASSWORD_LENGTH = 8
    }

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

    /**
     * Local validation for email and password
     */
    fun validateFields(
        email: String,
        password: String,
        validationPurpose: ValidationPurpose,
    ) {
        val isSuccessValidation = validateEmail(email) and validatePassword(password)

        if (isSuccessValidation) {
            when (validationPurpose) {
                SIGN_UP_VALIDATION -> { signUpUser(email, password) }
                SIGN_IN_VALIDATION -> { signInUser(email, password) }
            }
        }
    }

    private fun validateEmail(
        email: String,
    ): Boolean {
        return when {
            email.isEmpty() -> {
                sendViewEvent(EmailValidation(EMPTY_EMAIL))
                false
            }
            email.isEmailValid().not() -> {
                sendViewEvent(EmailValidation(NOT_VALID_EMAIL))
                false
            }
            else -> {
                sendViewEvent(EmailValidation(SUCCESS_EMAIL_VALIDATION))
                true
            }
        }
    }

    private fun validatePassword(
        password: String,
    ): Boolean {
        return when {
            password.isEmpty() -> {
                sendViewEvent(PasswordValidation(EMPTY_PASSWORD))
                false
            }
            password.length < MIN_PASSWORD_LENGTH -> {
                sendViewEvent(PasswordValidation(NOT_VALID_PASSWORD))
                false
            }
            else -> {
                sendViewEvent(PasswordValidation(SUCCESS_PASSWORD_VALIDATION))
                true
            }
        }
    }

    private fun signInUser(
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

    private fun signUpUser(
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

    /**
     * For security reasons, we should not check
     * the email by mask and password length while user Sing In
     *
     * That's why we should separate validation for Sign Up and Sing In
     */
    enum class ValidationPurpose {
        SIGN_UP_VALIDATION,
        SIGN_IN_VALIDATION,
    }
}