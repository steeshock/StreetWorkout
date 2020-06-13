package com.example.android.streetworkout.ui.places

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.android.streetworkout.databinding.FragmentPlacesBinding

class PlacesFragment : Fragment() {

    private lateinit var placesViewModel: PlacesViewModel

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {

        placesViewModel = ViewModelProviders.of(this).get(PlacesViewModel::class.java)

        val binding: FragmentPlacesBinding  = FragmentPlacesBinding.inflate(inflater, container, false)

        binding.viewmodel = placesViewModel

        return binding.root
    }
}