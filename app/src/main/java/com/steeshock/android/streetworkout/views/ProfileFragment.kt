package com.steeshock.android.streetworkout.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.steeshock.android.streetworkout.databinding.FragmentProfileBinding
import com.steeshock.android.streetworkout.viewmodels.ProfileViewModel

class ProfileFragment : Fragment() {

    private lateinit var profileViewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        profileViewModel = ViewModelProviders.of(this).get(ProfileViewModel::class.java)

        val binding: FragmentProfileBinding =
            FragmentProfileBinding.inflate(inflater, container, false)

        binding.viewmodel = profileViewModel

        return binding.root
    }
}