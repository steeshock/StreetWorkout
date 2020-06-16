package com.example.android.streetworkout.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.android.streetworkout.databinding.FragmentTrainingBinding
import com.example.android.streetworkout.viewmodels.TraningViewModel

class TraningFragment : Fragment() {

    private lateinit var traningViewModel: TraningViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {

        traningViewModel = ViewModelProviders.of(this).get(TraningViewModel::class.java)

        val binding: FragmentTrainingBinding = FragmentTrainingBinding.inflate(inflater, container, false)

        binding.viewmodel = traningViewModel

        return binding.root
    }
}