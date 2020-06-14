package com.example.android.streetworkout.ui.places

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.android.streetworkout.databinding.FragmentPlacesBinding
import com.example.android.streetworkout.model.PlaceObject

class PlacesFragment : Fragment() {

    private lateinit var placesViewModel: PlacesViewModel
    private lateinit var fragmentPlacesBinding: FragmentPlacesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        retainInstance = true
    }

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {

        placesViewModel = ViewModelProviders.of(this).get(PlacesViewModel::class.java)

        fragmentPlacesBinding  = FragmentPlacesBinding.inflate(inflater, container, false)

        return fragmentPlacesBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val placesAdapter = PlaceAdapter(placesViewModel.itemsMock, object : PlaceAdapter.Callback {
            override fun onItemClicked(item: PlaceObject) {
                Toast.makeText(view.context, item.description, Toast.LENGTH_SHORT).show()
            }
        })

        fragmentPlacesBinding.placesRecycler.adapter = placesAdapter
        fragmentPlacesBinding.placesRecycler.layoutManager = LinearLayoutManager(fragmentPlacesBinding.root.context)

        super.onViewCreated(view, savedInstanceState)

    }
}