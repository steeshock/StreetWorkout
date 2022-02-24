package com.steeshock.android.streetworkout.presentation.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.steeshock.android.streetworkout.R
import com.steeshock.android.streetworkout.common.BaseFragment
import com.steeshock.android.streetworkout.common.appComponent
import com.steeshock.android.streetworkout.databinding.FragmentProfileBinding
import com.steeshock.android.streetworkout.presentation.viewStates.auth.AuthViewEvent.*
import com.steeshock.android.streetworkout.presentation.viewStates.AuthViewState
import com.steeshock.android.streetworkout.presentation.viewStates.auth.*
import com.steeshock.android.streetworkout.presentation.viewStates.auth.EmailValidationResult.*
import com.steeshock.android.streetworkout.presentation.viewStates.auth.PasswordValidationResult.*
import com.steeshock.android.streetworkout.presentation.viewStates.auth.SignInResponse.*
import com.steeshock.android.streetworkout.presentation.viewmodels.ProfileViewModel
import com.steeshock.android.streetworkout.presentation.viewmodels.ProfileViewModel.ValidationPurpose.*
import com.steeshock.android.streetworkout.utils.extensions.gone
import com.steeshock.android.streetworkout.utils.extensions.toVisibility
import com.steeshock.android.streetworkout.utils.extensions.visible
import javax.inject.Inject

class ProfileFragment : BaseFragment() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    private val viewModel: ProfileViewModel by viewModels { factory }

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun injectComponent() {
        context?.appComponent?.provideProfileComponent()?.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        (container?.context as MainActivity).setSupportActionBar(binding.toolbar)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.viewState.observe(this) {
            renderViewState(it)
        }

        viewModel.viewEvent.observe(this) {
            renderViewEvent(it)
        }
        viewModel.requestAuthState()
    }

    private fun renderViewState(viewState: AuthViewState) {
        binding.progress.visibility = viewState.isLoading.toVisibility()
    }

    private fun renderViewEvent(viewEvent: AuthViewEvent) {
        when(viewEvent) {
            is SignUpResult,
            is SignInResult,
            UnknownError -> {
                showSnackbar(viewEvent)
            }
            is EmailValidation -> {
                showEmailValidationError(viewEvent.result)
            }
            is PasswordValidation -> {
                showPasswordValidationError(viewEvent.result)
            }
        }
    }

    // TODO("Показывать другой вью элемент")
    private fun showSnackbar(viewEvent: AuthViewEvent) {
        val message = when(viewEvent) {
            is SignUpResult -> {
                handleSignUpResult(viewEvent)
            }
            is SignInResult -> {
                handleSignInResult(viewEvent)
            }
            else -> {
                getString(R.string.unknown_error)
            }
        }
        val snackbar = Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
        snackbar.anchorView = getBottomBaseline()
        snackbar.show()
    }

    private fun handleSignUpResult(
        viewEvent: SignUpResult,
    ) = when (viewEvent.result) {
        is SignUpResponse.SuccessSignUp -> {
            getString(R.string.success_sign_up, viewEvent.result.email)
        }
        is SignUpResponse.UserCollisionError -> {
            showEmailValidationError(EXISTING_EMAIL)
            getString(R.string.sign_error)
        }
    }

    private fun handleSignInResult(
        viewEvent: SignInResult,
    ) = when (viewEvent.result) {
        is SuccessSignIn -> {
            setupProfilePage()
            getString(R.string.success_sign_in, viewEvent.result.email)
        }
        is InvalidUserError -> {
            showEmailValidationError(INVALID_EMAIL)
            getString(R.string.sign_error)
        }
        is InvalidCredentialsError -> {
            showCredentialsError()
            getString(R.string.sign_error)
        }
        is UserNotAuthorized -> {
            setupLoginPage()
            getString(R.string.sign_prompt_message)
        }
    }

    private fun showPasswordValidationError(result: PasswordValidationResult) {
        binding.loginLayout.passwordInput.error = when (result) {
            EMPTY_PASSWORD -> resources.getString(R.string.empty_password_error)
            NOT_VALID_PASSWORD -> resources.getString(R.string.not_valid_password_error)
            SUCCESS_PASSWORD_VALIDATION -> null
        }
    }

    private fun showEmailValidationError(result: EmailValidationResult) {
        binding.loginLayout.emailInput.error = when (result) {
            EMPTY_EMAIL -> resources.getString(R.string.empty_email_error)
            NOT_VALID_EMAIL -> resources.getString(R.string.not_valid_email_error)
            EXISTING_EMAIL -> resources.getString(R.string.existing_user_email_error)
            INVALID_EMAIL -> resources.getString(R.string.invalid_user_email_error)
            SUCCESS_EMAIL_VALIDATION -> null
        }
    }

    private fun showCredentialsError() {
        binding.loginLayout.emailInput.error = resources.getString(R.string.wrong_email_error)
        binding.loginLayout.passwordInput.error = resources.getString(R.string.wrong_password_error)
    }

    private fun setupLoginPage() {
        binding.profileLayout.root.gone()
        binding.loginLayout.root.visible()

        binding.loginLayout.signUpButton.setOnClickListener {
            viewModel.validateFields(
                email = getEmail(),
                password = getPassword(),
                validationPurpose = SIGN_UP_VALIDATION,
            )
        }

        binding.loginLayout.signInButton.setOnClickListener {
            viewModel.validateFields(
                email = getEmail(),
                password = getPassword(),
                validationPurpose = SIGN_IN_VALIDATION,
            )
        }
    }

    private fun setupProfilePage() {
        binding.loginLayout.root.gone()
        binding.profileLayout.root.visible()
    }

    private fun getEmail() = binding.loginLayout.emailEditText.text.toString()

    private fun getPassword() = binding.loginLayout.passwordEditText.text.toString()

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}