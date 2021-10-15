package com.steeshock.android.streetworkout.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.steeshock.android.streetworkout.common.App
import com.steeshock.android.streetworkout.common.BaseFragment
import com.steeshock.android.streetworkout.common.appComponent
import com.steeshock.android.streetworkout.databinding.FragmentProfileBinding
import com.steeshock.android.streetworkout.viewmodels.CustomPlacesViewModelFactory
import com.steeshock.android.streetworkout.viewmodels.CustomProfileViewModelFactory
import com.steeshock.android.streetworkout.viewmodels.PlacesViewModel
import com.steeshock.android.streetworkout.viewmodels.ProfileViewModel
import javax.inject.Inject

class ProfileFragment : BaseFragment() {

    @Inject
    lateinit var factory: CustomProfileViewModelFactory

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