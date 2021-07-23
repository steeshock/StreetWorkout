package com.steeshock.android.streetworkout.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.steeshock.android.streetworkout.databinding.FragmentPlacesBinding
import com.steeshock.android.streetworkout.databinding.FragmentProfileBinding
import com.steeshock.android.streetworkout.viewmodels.ProfileViewModel

class ProfileFragment : Fragment() {

    private var _fragmentProfileBinding: FragmentProfileBinding? = null
    private val fragmentProfileBinding get() = _fragmentProfileBinding!!

    private lateinit var profileViewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        profileViewModel = ViewModelProviders.of(this).get(ProfileViewModel::class.java)

        _fragmentProfileBinding = FragmentProfileBinding.inflate(inflater, container, false)

        fragmentProfileBinding.viewmodel = profileViewModel

        return fragmentProfileBinding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _fragmentProfileBinding = null
    }
}