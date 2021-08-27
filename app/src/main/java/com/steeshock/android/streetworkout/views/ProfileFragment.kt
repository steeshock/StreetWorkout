package com.steeshock.android.streetworkout.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.steeshock.android.streetworkout.common.BaseFragment
import com.steeshock.android.streetworkout.databinding.FragmentProfileBinding
import com.steeshock.android.streetworkout.utils.InjectorUtils
import com.steeshock.android.streetworkout.viewmodels.ProfileViewModel

class ProfileFragment : BaseFragment() {

    private val profileViewModel: ProfileViewModel by viewModels {
        InjectorUtils.provideProfileViewModelFactory(requireActivity())
    }

    private var _fragmentProfileBinding: FragmentProfileBinding? = null
    private val fragmentProfileBinding get() = _fragmentProfileBinding!!

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