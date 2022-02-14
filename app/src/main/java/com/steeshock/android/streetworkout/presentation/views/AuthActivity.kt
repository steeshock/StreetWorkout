package com.steeshock.android.streetworkout.presentation.views

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.steeshock.android.streetworkout.R
import com.steeshock.android.streetworkout.common.appComponent
import com.steeshock.android.streetworkout.databinding.ActivityAuthBinding
import com.steeshock.android.streetworkout.presentation.viewStates.AuthViewEvent
import com.steeshock.android.streetworkout.presentation.viewStates.AuthViewState
import com.steeshock.android.streetworkout.presentation.viewmodels.AuthViewModel
import com.steeshock.android.streetworkout.presentation.viewStates.AuthViewEvent.*
import com.steeshock.android.streetworkout.utils.extensions.toVisibility
import javax.inject.Inject

class AuthActivity : AppCompatActivity() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    private val viewModel: AuthViewModel by viewModels { factory }

    private var _binding: ActivityAuthBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        application?.appComponent?.provideAuthComponent()?.inject(this)
        _binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
    }

    private fun renderViewState(viewState: AuthViewState) {
        binding.progress.visibility = viewState.isLoading.toVisibility()
    }

    private fun renderViewEvent(viewEvent: AuthViewEvent) {
        when(viewEvent) {
            is SuccessSignUp -> {
                showSnackbar(viewEvent.userEmail)
                startActivity(Intent(this, HomeActivity::class.java, ))
            }
            is SuccessAuthorization -> {
                startActivity(Intent(this, HomeActivity::class.java, ))
            }
        }
    }

    // TODO("Показывать другой вью элемент")
    private fun showSnackbar(message: String?) {
        Snackbar.make(
            binding.root,
            if (message != null) getString(R.string.success_sign_up, message) else getString(R.string.failed_message),
            Snackbar.LENGTH_LONG
        ).show()
    }

    override fun onStart() {
        super.onStart()
        viewModel.requestAuthState()
    }

    private fun getEmail() = binding.emailEditText.text.toString()

    private fun getPassword() = binding.emailEditText.text.toString()
}