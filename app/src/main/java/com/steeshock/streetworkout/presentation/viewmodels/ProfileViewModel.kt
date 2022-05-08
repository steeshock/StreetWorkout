package com.steeshock.streetworkout.presentation.viewmodels

import androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.steeshock.streetworkout.data.model.User
import com.steeshock.streetworkout.data.repository.implementation.DataStoreRepository.PreferencesKeys.NIGHT_MODE_PREFERENCES_KEY
import com.steeshock.streetworkout.data.repository.interfaces.IDataStoreRepository
import com.steeshock.streetworkout.domain.IFavoritesInteractor
import com.steeshock.streetworkout.domain.ILoginInteractor
import com.steeshock.streetworkout.presentation.delegates.ViewEventDelegate
import com.steeshock.streetworkout.presentation.delegates.ViewEventDelegateImpl
import com.steeshock.streetworkout.presentation.delegates.ViewStateDelegate
import com.steeshock.streetworkout.presentation.delegates.ViewStateDelegateImpl
import com.steeshock.streetworkout.presentation.viewStates.auth.AuthViewEvent
import com.steeshock.streetworkout.presentation.viewStates.auth.AuthViewEvent.*
import com.steeshock.streetworkout.presentation.viewStates.auth.AuthViewState
import com.steeshock.streetworkout.presentation.viewStates.auth.EmailValidationResult.*
import com.steeshock.streetworkout.presentation.viewStates.auth.PasswordValidationResult.*
import com.steeshock.streetworkout.presentation.viewStates.auth.SignInResponse.*
import com.steeshock.streetworkout.presentation.viewStates.auth.SignUpResponse.*
import com.steeshock.streetworkout.services.auth.IAuthService
import com.steeshock.streetworkout.services.auth.IAuthService.SignPurpose
import com.steeshock.streetworkout.services.auth.IAuthService.SignPurpose.SIGN_IN
import com.steeshock.streetworkout.services.auth.IAuthService.SignPurpose.SIGN_UP
import com.steeshock.streetworkout.utils.extensions.getThemeByIndex
import com.steeshock.streetworkout.utils.extensions.isEmailValid
import kotlinx.coroutines.*
import javax.inject.Inject

class ProfileViewModel @Inject constructor(
    private val authService: IAuthService,
    private val dataStoreRepository: IDataStoreRepository,
    private val favoritesInteractor: IFavoritesInteractor,
    private val loginInteractor: ILoginInteractor,
) : ViewModel(),
    ViewEventDelegate<AuthViewEvent> by ViewEventDelegateImpl(),
    ViewStateDelegate<AuthViewState> by ViewStateDelegateImpl({ AuthViewState() }) {

    private companion object {
        private const val MIN_PASSWORD_LENGTH = 8
    }

    fun requestAuthState(signPurpose: SignPurpose) = viewModelScope.launch(Dispatchers.IO) {
        updateViewState(postValue = true) {
            copy(
                isLoading = true,
                signPurpose = signPurpose,
            )
        }
        if (authService.isUserAuthorized) {
            postViewEvent(
                event = SignInResult(
                    SuccessSignIn(
                        User(
                            displayName = authService.currentUserDisplayName,
                            email = authService.currentUserEmail,
                        )
                    )
                ),
            )
        } else {
            postViewEvent(SignInResult(UserNotAuthorized))
        }
        updateViewState(postValue = true) {
            copy(
                isLoading = false,
                signPurpose = signPurpose,
            )
        }
    }

    /**
     * Local validation for email and password
     */
    fun onValidateForm(
        email: String,
        password: String,
    ) {
        val isSuccessValidation =
            validateEmail(email) and validatePassword(password, viewState.value?.signPurpose)

        if (isSuccessValidation) {
            viewState.value?.signPurpose?.let { signPurpose ->
                loginInteractor
                signUser(email, password, signPurpose)
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

    private fun signUser(
        email: String,
        password: String,
        signPurpose: SignPurpose,
    ) = viewModelScope.launch(Dispatchers.IO) {
        updateViewState(postValue = true) { copy(isLoading = true) }
        try {
            coroutineScope {
                val user = loginInteractor.signUser(email, password, signPurpose)
                favoritesInteractor.syncFavoritePlaces()

                val event = when (signPurpose) {
                    SIGN_UP -> SignUpResult(SuccessSignUp(user))
                    SIGN_IN -> SignInResult(SuccessSignIn(user))
                }

                withContext(Dispatchers.Main) {
                    updateViewState { copy(isLoading = false) }
                    sendViewEvent(event)
                }
            }
        } catch (e: Exception) {
            authService.signOut()
            updateViewState(postValue = true) { copy(isLoading = false) }
            handleException(e, signPurpose)
        }
    }

    private fun handleException(
        exception: Exception,
        signPurpose: SignPurpose,
    ) = viewModelScope.launch(Dispatchers.IO)  {
        withContext(Dispatchers.Main) {
            when (exception) {
                is FirebaseAuthUserCollisionException -> {
                    sendViewEvent(SignUpResult(UserCollisionError))
                }
                is FirebaseAuthInvalidUserException -> {
                    sendViewEvent(SignInResult(InvalidUserError))
                }
                is FirebaseAuthInvalidCredentialsException -> {
                    when (signPurpose) {
                        SIGN_IN -> {
                            sendViewEvent(SignInResult(InvalidCredentialsError))
                        }
                        SIGN_UP -> {
                            sendViewEvent(SignUpResult(InvalidEmailError))
                        }
                    }
                }
                else -> {
                    sendViewEvent(UnknownError)
                }
            }
        }
    }

    fun signOut() = viewModelScope.launch(Dispatchers.IO) {
        updateViewState(postValue = true) { copy(isLoading = true) }
        authService.signOut()
        favoritesInteractor.resetFavorites()
        updateViewState(postValue = true) { copy(isLoading = false) }
        postViewEvent(SignOut)
    }

    fun changeSignPurpose(currentSignPurpose: SignPurpose) {
        when (currentSignPurpose) {
            SIGN_UP -> {
                updateViewState { copy(signPurpose = SIGN_IN) }
            }
            SIGN_IN -> {
                updateViewState { copy(signPurpose = SIGN_UP) }
            }
        }
    }

    fun changeAppTheme(checkedItem: Int) = viewModelScope.launch(Dispatchers.IO) {
        val newThemeValue = getThemeByIndex(checkedItem)
        dataStoreRepository.putInt(NIGHT_MODE_PREFERENCES_KEY, newThemeValue)
        withContext(Dispatchers.Main) {
            setDefaultNightMode(newThemeValue)
        }
    }
}