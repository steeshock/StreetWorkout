package com.steeshock.android.streetworkout.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.steeshock.android.streetworkout.presentation.viewStates.AuthViewState
import com.steeshock.android.streetworkout.presentation.viewStates.SingleLiveEvent
import com.steeshock.android.streetworkout.presentation.viewStates.auth.AuthViewEvent
import com.steeshock.android.streetworkout.presentation.viewStates.auth.AuthViewEvent.*
import com.steeshock.android.streetworkout.presentation.viewStates.auth.EmailValidationResult.*
import com.steeshock.android.streetworkout.presentation.viewStates.auth.PasswordValidationResult.*
import com.steeshock.android.streetworkout.presentation.viewStates.auth.SignInResponse
import com.steeshock.android.streetworkout.presentation.viewStates.auth.SignInResponse.*
import com.steeshock.android.streetworkout.presentation.viewStates.auth.SignUpResponse.SuccessSignUp
import com.steeshock.android.streetworkout.presentation.viewStates.auth.SignUpResponse.UserCollisionError
import com.steeshock.android.streetworkout.presentation.viewmodels.ProfileViewModel.ValidationPurpose.SIGN_IN_VALIDATION
import com.steeshock.android.streetworkout.presentation.viewmodels.ProfileViewModel.ValidationPurpose.SIGN_UP_VALIDATION
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
                event = SignInResult(
                    result = SuccessSignIn(authService.getUserEmail())
                ),
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
        val isSuccessValidation =
            validateEmail(email) and validatePassword(password, validationPurpose)

        if (isSuccessValidation) {
            when (validationPurpose) {
                SIGN_UP_VALIDATION -> {
                    signUpUser(email, password)
                }
                SIGN_IN_VALIDATION -> {
                    signInUser(email, password)
                }
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

    /**
     * Check password length only for Sign Up
     */
    private fun validatePassword(
        password: String,
        validationPurpose: ValidationPurpose,
    ): Boolean {
        return when {
            password.isEmpty() -> {
                sendViewEvent(PasswordValidation(EMPTY_PASSWORD))
                false
            }
            validationPurpose == SIGN_UP_VALIDATION && password.length < MIN_PASSWORD_LENGTH -> {
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
                sendViewEvent(SignInResult(SuccessSignIn(email)))
            },
            onError = {
                mutableViewState.updateState(postValue = true) { copy(isLoading = false) }
                handleException(it)
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
                sendViewEvent(SignUpResult(SuccessSignUp(email)))
            },
            onError = {
                mutableViewState.updateState(postValue = true) { copy(isLoading = false) }
                handleException(it)
            }
        )
    }

    private fun handleException(exception: Exception) {
        when (exception) {
            is FirebaseAuthUserCollisionException -> {
                sendViewEvent(SignUpResult(UserCollisionError))
            }
            is FirebaseAuthInvalidUserException -> {
                sendViewEvent(SignInResult(InvalidUserError))
            }
            is FirebaseAuthInvalidCredentialsException -> {
                sendViewEvent(SignInResult(InvalidCredentialsError))
            }
            else -> {
                sendViewEvent(UnknownError)
            }
        }
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
     * the password length while user Sing In
     *
     * That's why we should separate validation for Sign Up and Sing In
     */
    enum class ValidationPurpose {
        SIGN_UP_VALIDATION,
        SIGN_IN_VALIDATION,
    }
}