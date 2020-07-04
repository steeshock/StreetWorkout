package com.example.android.streetworkout.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.example.android.streetworkout.R
import com.example.android.streetworkout.data.model.PlaceObject
import com.example.android.streetworkout.databinding.FragmentAddPlaceBinding
import com.example.android.streetworkout.utils.InjectorUtils
import com.example.android.streetworkout.viewmodels.AddPlaceViewModel


class AddPlaceFragment : Fragment() {

    private val addPlaceViewModel: AddPlaceViewModel by viewModels {
        InjectorUtils.provideAddPlaceViewModelFactory(requireActivity())
    }

    private lateinit var fragmentAddPlaceBinding: FragmentAddPlaceBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        fragmentAddPlaceBinding = FragmentAddPlaceBinding.inflate(inflater, container, false)
        fragmentAddPlaceBinding.viewmodel = addPlaceViewModel
        fragmentAddPlaceBinding.lifecycleOwner = this

        fragmentAddPlaceBinding.toolbar.setNavigationOnClickListener { view ->
            view.findNavController().navigateUp()
        }

        fragmentAddPlaceBinding.toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_done -> {
                    addNewPlace()
                    true
                }
                else -> false
            }
        }

        return fragmentAddPlaceBinding.root
    }

    private fun addNewPlace() {
        addPlaceViewModel.insert(
            PlaceObject(
                "https://picsum.photos/30${(0..9).random()}/200",
                title = fragmentAddPlaceBinding.placeTitle.text.toString(),
                description = fragmentAddPlaceBinding.placeDescription.text.toString(),
                position = fragmentAddPlaceBinding.placePosition.text.toString(),
                address = fragmentAddPlaceBinding.placeAddress.text.toString()
            )
        )
    }
}