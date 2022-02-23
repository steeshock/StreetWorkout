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
import com.steeshock.android.streetworkout.presentation.viewStates.AuthViewEvent
import com.steeshock.android.streetworkout.presentation.viewStates.AuthViewEvent.*
import com.steeshock.android.streetworkout.presentation.viewStates.AuthViewState
import com.steeshock.android.streetworkout.presentation.viewStates.EmailValidationResult
import com.steeshock.android.streetworkout.presentation.viewStates.PasswordValidationResult
import com.steeshock.android.streetworkout.presentation.viewmodels.ProfileViewModel
import com.steeshock.android.streetworkout.presentation.viewmodels.ProfileViewModel.ValidationPurpose.*
import com.steeshock.android.streetworkout.utils.extensions.toVisibility
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

        binding.signUpButton.setOnClickListener {
            viewModel.validateFields(
                email = getEmail(),
                password = getPassword(),
                validationPurpose = SIGN_UP_VALIDATION,
            )
        }

        binding.signInButton.setOnClickListener {
            viewModel.validateFields(
                email = getEmail(),
                password = getPassword(),
                validationPurpose = SIGN_IN_VALIDATION,
            )
        }

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
            is SuccessSignUp,
            is SuccessSignIn -> {
                showSnackbar(viewEvent)
            }
            is EmailValidation -> {
                binding.emailInput.error = when(viewEvent.result)  {
                    EmailValidationResult.EMPTY_EMAIL -> resources.getString(R.string.empty_email_error)
                    EmailValidationResult.NOT_VALID_EMAIL -> resources.getString(R.string.not_valid_email_error)
                    EmailValidationResult.SUCCESS_EMAIL_VALIDATION -> null
                }
            }
            is PasswordValidation -> {
                binding.passwordInput.error = when(viewEvent.result)  {
                    PasswordValidationResult.EMPTY_PASSWORD -> resources.getString(R.string.empty_password_error)
                    PasswordValidationResult.NOT_VALID_PASSWORD -> resources.getString(R.string.not_valid_password_error)
                    PasswordValidationResult.SUCCESS_PASSWORD_VALIDATION -> null
                }
            }
        }
    }

    // TODO("Показывать другой вью элемент")
    private fun showSnackbar(viewEvent: AuthViewEvent) {
        val message = when(viewEvent) {
            is SuccessSignUp -> {
                getString(R.string.success_sign_up, viewEvent.userEmail)
            }
            is SuccessSignIn -> {
                getString(R.string.success_sign_in, viewEvent.userEmail)
            }
            else -> ""
        }
        val snackbar = Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
        snackbar.anchorView = getBaseline()
        snackbar.show()
    }

    private fun getEmail() = binding.emailEditText.text.toString()

    private fun getPassword() = binding.passwordEditText.text.toString()

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}