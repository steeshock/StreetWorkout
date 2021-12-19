package com.steeshock.android.streetworkout.presentation.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.steeshock.android.streetworkout.common.BaseFragment
import com.steeshock.android.streetworkout.common.appComponent
import com.steeshock.android.streetworkout.data.factories.ProfileViewModelFactory
import com.steeshock.android.streetworkout.databinding.FragmentProfileBinding
import com.steeshock.android.streetworkout.presentation.viewmodels.ProfileViewModel
import javax.inject.Inject

class ProfileFragment : BaseFragment() {

    @Inject
    lateinit var factory: ProfileViewModelFactory

    private val profileViewModel: ProfileViewModel by viewModels { factory }

    private var _fragmentProfileBinding: FragmentProfileBinding? = null
    private val fragmentProfileBinding get() = _fragmentProfileBinding!!

    override fun injectComponent() {
        context?.appComponent?.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _fragmentProfileBinding = FragmentProfileBinding.inflate(inflater, container, false)

        fragmentProfileBinding.viewmodel = profileViewModel

        return fragmentProfileBinding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _fragmentProfileBinding = null
    }
}