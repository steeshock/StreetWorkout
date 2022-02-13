package com.steeshock.android.streetworkout.presentation.views

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.steeshock.android.streetworkout.common.appComponent
import com.steeshock.android.streetworkout.databinding.ActivityAuthBinding
import com.steeshock.android.streetworkout.presentation.viewmodels.AuthViewModel
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

        binding.authButton.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java, ))
        }
    }
}