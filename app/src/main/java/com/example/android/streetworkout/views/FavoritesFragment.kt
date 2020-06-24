package com.example.android.streetworkout.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.android.streetworkout.databinding.FragmentFavoritesBinding
import com.example.android.streetworkout.viewmodels.FavoritesViewModel

class FavoritesFragment : Fragment() {

    private lateinit var favoritesViewModel: FavoritesViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        favoritesViewModel = ViewModelProviders.of(this).get(FavoritesViewModel::class.java)

        val binding: FragmentFavoritesBinding =
            FragmentFavoritesBinding.inflate(inflater, container, false)

        binding.viewmodel = favoritesViewModel

        return binding.root
    }
}