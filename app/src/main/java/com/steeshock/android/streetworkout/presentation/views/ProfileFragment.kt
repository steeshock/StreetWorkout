package com.steeshock.android.streetworkout.presentation.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.steeshock.android.streetworkout.common.BaseFragment
import com.steeshock.android.streetworkout.common.appComponent
import com.steeshock.android.streetworkout.databinding.FragmentProfileBinding
import com.steeshock.android.streetworkout.presentation.viewmodels.ProfileViewModel
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
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}