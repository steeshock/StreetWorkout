package com.steeshock.streetworkout.presentation.views

import android.os.Bundle
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.view.*
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.steeshock.streetworkout.R
import com.steeshock.streetworkout.common.BaseFragment
import com.steeshock.streetworkout.common.appComponent
import com.steeshock.streetworkout.data.model.User
import com.steeshock.streetworkout.databinding.FragmentProfileBinding
import com.steeshock.streetworkout.presentation.viewStates.AuthViewState
import com.steeshock.streetworkout.presentation.viewStates.auth.AuthViewEvent
import com.steeshock.streetworkout.presentation.viewStates.auth.AuthViewEvent.*
import com.steeshock.streetworkout.presentation.viewStates.auth.EmailValidationResult
import com.steeshock.streetworkout.presentation.viewStates.auth.EmailValidationResult.*
import com.steeshock.streetworkout.presentation.viewStates.auth.PasswordValidationResult
import com.steeshock.streetworkout.presentation.viewStates.auth.PasswordValidationResult.*
import com.steeshock.streetworkout.presentation.viewStates.auth.SignInResponse.*
import com.steeshock.streetworkout.presentation.viewStates.auth.SignUpResponse
import com.steeshock.streetworkout.presentation.viewmodels.ProfileViewModel
import com.steeshock.streetworkout.presentation.viewmodels.ProfileViewModel.SignPurpose
import com.steeshock.streetworkout.presentation.viewmodels.ProfileViewModel.SignPurpose.SIGN_IN
import com.steeshock.streetworkout.presentation.viewmodels.ProfileViewModel.SignPurpose.SIGN_UP
import com.steeshock.streetworkout.utils.extensions.*
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

        initLoginPage()
        initProfilePage()

        viewModel.viewState.observe(viewLifecycleOwner) {
            renderViewState(it)
        }

        viewModel.viewEvent.observe(viewLifecycleOwner) {
            renderViewEvent(it)
        }

        val signPurposeFromNavigationArgs = getSignPurposeArgs()
        viewModel.requestAuthState(signPurposeFromNavigationArgs)
    }

    private fun initLoginPage() {
        binding.loginLayout.signButton.setOnClickListener {
            viewModel.validateFields(
                email = getEmail(),
                password = getPassword(),
            )
        }
    }

    private fun initProfilePage() {
        binding.profileLayout.logoutButton.setOnClickListener {
            showAlertDialog(
                title = getString(R.string.action_confirm_title),
                message = getString(R.string.logout_description),
                positiveText = getString(R.string.logout_button),
                negativeText = getString(R.string.cancel_item),
                onPositiveAction = { viewModel.signOut() },
            )
        }
    }

    private fun renderViewState(viewState: AuthViewState) {
        binding.progress.root.visibility = viewState.isLoading.toVisibility()
        setupSignButtonState(viewState.signPurpose)
    }

    private fun setupSignButtonState(signPurpose: SignPurpose) {
        var promptText = ""
        var promptLink = ""

        when (signPurpose) {
            SIGN_UP -> {
                binding.loginLayout.signButton.text = getString(R.string.sign_up_button_title)
                promptText = getString(R.string.authorization_prompt_message)
                promptLink = getString(R.string.authorization_link)
            }
            SIGN_IN -> {
                binding.loginLayout.signButton.text = getString(R.string.sign_in_button_title)
                promptText = getString(R.string.registration_prompt_message)
                promptLink = getString(R.string.registration_link)
            }
        }
        binding.loginLayout.signPromptTextView.apply {
            movementMethod = LinkMovementMethod.getInstance()
            val textWithLink = SpannableString(promptText).apply {
                setLinkSpan(
                    link = promptLink,
                    onClick = {
                        viewModel.changeSignPurpose(signPurpose)
                    }
                )
            }
            text = textWithLink
        }
    }

    private fun renderViewEvent(viewEvent: AuthViewEvent) {
        when (viewEvent) {
            is SignUpResult,
            is SignInResult,
            is UnknownError,
            -> {
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
        val message = when (viewEvent) {
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
        showSnackbar(message)
    }

    private fun handleSignUpResult(
        viewEvent: SignUpResult,
    ) = when (viewEvent.result) {
        is SignUpResponse.SuccessSignUp -> {
            getString(R.string.success_sign_up, viewEvent.result.user?.email)
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
            showProfilePage(viewEvent.result.user)
            getString(R.string.success_sign_in, viewEvent.result.user?.email)
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
            showLoginPage()
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

    private fun showLoginPage() {
        binding.profileLayout.root.gone()
        binding.loginLayout.root.visible()
    }

    private fun showProfilePage(user: User?) {
        binding.loginLayout.root.gone()
        binding.profileLayout.root.visible()

        binding.profileLayout.displayNameTextView.text = user?.displayName
        binding.profileLayout.emailTextView.text = user?.email
    }

    private fun getEmail() = binding.loginLayout.emailEditText.text.toString()

    private fun getPassword() = binding.loginLayout.passwordEditText.text.toString()

    private fun getSignPurposeArgs() = run {
        arguments?.get("sign_purpose")?.let {
            SignPurpose.fromString(it.toString())
        } ?: SIGN_UP
    }

    // region Menu
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.profile_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_change_theme -> {
                showThemeChangeDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    // endregion

    private fun showThemeChangeDialog() {
        getAlertDialogBuilder(
            title = getString(R.string.select_theme_dialog_title),
        )
            .setSingleChoiceItems(
                resources.getStringArray(R.array.themesSelector),
                getIndexByThemeName()
            ) { dialog, checkedItem ->
                dialog.dismiss()
                viewModel.changeAppTheme(checkedItem)
            }
            .create()
            .show()
    }

    override fun onDestroyView() {
       _binding = null
       super.onDestroyView()    
    }
}