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
import com.steeshock.android.streetworkout.presentation.viewStates.AuthViewEvent.SuccessAuthorization
import com.steeshock.android.streetworkout.presentation.viewStates.AuthViewEvent.SuccessSignUp
import com.steeshock.android.streetworkout.presentation.viewStates.AuthViewState
import com.steeshock.android.streetworkout.presentation.viewmodels.ProfileViewModel
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
            viewModel.signUpNewUser(
                email = getEmail(),
                password = getPassword(),
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
            is SuccessSignUp -> {
                showSnackbar(viewEvent.userEmail)
            }
            is SuccessAuthorization -> {

            }
        }
    }

    // TODO("Показывать другой вью элемент")
    private fun showSnackbar(message: String?) {
        val snackbar = Snackbar.make(
            binding.root,
            if (message != null) getString(R.string.success_sign_up, message) else getString(R.string.failed_message),
            Snackbar.LENGTH_LONG
        )
        snackbar.anchorView = getBaseline()
        snackbar.show()
    }

    private fun getEmail() = binding.emailEditText.text.toString()

    private fun getPassword() = binding.emailEditText.text.toString()

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}