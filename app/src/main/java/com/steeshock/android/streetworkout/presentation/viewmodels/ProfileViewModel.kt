package com.steeshock.android.streetworkout.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.steeshock.android.streetworkout.data.model.User
import com.steeshock.android.streetworkout.presentation.viewStates.AuthViewState
import com.steeshock.android.streetworkout.presentation.viewStates.SingleLiveEvent
import com.steeshock.android.streetworkout.presentation.viewStates.auth.AuthViewEvent
import com.steeshock.android.streetworkout.presentation.viewStates.auth.AuthViewEvent.*
import com.steeshock.android.streetworkout.presentation.viewStates.auth.EmailValidationResult.*
import com.steeshock.android.streetworkout.presentation.viewStates.auth.PasswordValidationResult.*
import com.steeshock.android.streetworkout.presentation.viewStates.auth.SignInResponse.*
import com.steeshock.android.streetworkout.presentation.viewStates.auth.SignUpResponse.SuccessSignUp
import com.steeshock.android.streetworkout.presentation.viewStates.auth.SignUpResponse.UserCollisionError
import com.steeshock.android.streetworkout.presentation.viewmodels.ProfileViewModel.SignPurpose.SIGN_IN
import com.steeshock.android.streetworkout.presentation.viewmodels.ProfileViewModel.SignPurpose.SIGN_UP
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
        mutableViewState.updateState(postValue = true) { copy(isLoading = true) }
        if (authService.isUserAuthorized()) {
            sendViewEvent(
                postValue = true,
                event = SignInResult(
                    SuccessSignIn(
                        User(
                            displayName = authService.getDisplayName(),
                            email = authService.getUserEmail(),
                        )
                    )
                ),
            )
        } else {
            sendViewEvent(
                postValue = true,
                event = SignInResult(UserNotAuthorized),
            )
        }
        mutableViewState.updateState(postValue = true) { copy(isLoading = false) }
    }

    /**
     * Local validation for email and password
     */
    fun validateFields(
        email: String,
        password: String,
    ) {
        val isSuccessValidation =
            validateEmail(email) and validatePassword(password, viewState.value?.signPurpose)

        if (isSuccessValidation) {
            when (viewState.value?.signPurpose) {
                SIGN_UP -> {
                    signUpUser(email, password)
                }
                else -> {
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
        signPurpose: SignPurpose?,
    ): Boolean {
        return when {
            password.isEmpty() -> {
                sendViewEvent(PasswordValidation(EMPTY_PASSWORD))
                false
            }
            signPurpose == SIGN_UP && password.length < MIN_PASSWORD_LENGTH -> {
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
            onSuccess = { user ->
                mutableViewState.updateState(postValue = true) {
                    copy(isLoading = false)
                }
                sendViewEvent(
                    SignInResult(
                        SuccessSignIn(
                            User(
                                displayName = user?.displayName,
                                email = user?.email,
                            )
                        )
                    ),
                )
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
            onSuccess = { user ->
                mutableViewState.updateState(postValue = true) {
                    copy(isLoading = false)
                }
                sendViewEvent(
                    SignUpResult(
                        SuccessSignUp(
                            User(
                                displayName = user?.displayName,
                                email = user?.email,
                            )
                        )
                    ),
                )
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

    fun signOut() = viewModelScope.launch(Dispatchers.IO) {
        mutableViewState.updateState(postValue = true) { copy(isLoading = true) }
        authService.signOut()
        mutableViewState.updateState(postValue = true) { copy(isLoading = false) }
        sendViewEvent(
            postValue = true,
            event = SignInResult(UserNotAuthorized),
        )
    }

    fun changeSignPurpose(currentSignPurpose: SignPurpose) {
        when(currentSignPurpose) {
            SIGN_UP -> {
                mutableViewState.updateState { copy(signPurpose = SIGN_IN) }
            }
            SIGN_IN -> {
                mutableViewState.updateState { copy(signPurpose = SIGN_UP) }
            }
        }
    }

    /**
     * For security reasons, we should not check
     * the password length while user Sing In
     *
     * That's why we should separate validation for Sign Up and Sing In
     */
    enum class SignPurpose {
        SIGN_UP,
        SIGN_IN,
    }
}