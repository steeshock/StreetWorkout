package com.example.android.streetworkout.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.android.streetworkout.databinding.FragmentTrainingBinding
import com.example.android.streetworkout.viewmodels.TrainingViewModel

class TraningFragment : Fragment() {

    private lateinit var trainingViewModel: TrainingViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        trainingViewModel = ViewModelProviders.of(this).get(TrainingViewModel::class.java)

        val binding: FragmentTrainingBinding =
            FragmentTrainingBinding.inflate(inflater, container, false)

        binding.viewmodel = trainingViewModel

        return binding.root
    }
}