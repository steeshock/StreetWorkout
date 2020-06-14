package com.example.android.streetworkout.ui.places

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.android.streetworkout.BaseFragment
import com.example.android.streetworkout.R
import com.example.android.streetworkout.databinding.FragmentPlacesBinding
import com.example.android.streetworkout.data.model.PlaceObject
import com.google.android.material.floatingactionbutton.FloatingActionButton

class PlacesFragment : BaseFragment() {

    private var counter:Int = 0

    private lateinit var fab: FloatingActionButton
    private lateinit var placesViewModel: PlacesViewModel
    private lateinit var fragmentPlacesBinding: FragmentPlacesBinding

    override fun getViewModel(): ViewModel? = ViewModelProvider(this).get(PlacesViewModel::class.java)

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {

        placesViewModel = getViewModel() as PlacesViewModel

        fragmentPlacesBinding  = FragmentPlacesBinding.inflate(inflater, container, false)

        fragmentPlacesBinding.lifecycleOwner = this

        return fragmentPlacesBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val placesAdapter = PlaceAdapter(object : PlaceAdapter.Callback {
            override fun onItemClicked(item: PlaceObject) {
                Toast.makeText(view.context, item.description, Toast.LENGTH_SHORT).show()
            }
        })

        placesViewModel.allPlaces.observe(viewLifecycleOwner, Observer { places ->
            places?.let { placesAdapter.setPlaces(it) }
        })

        fab = view.findViewById(R.id.fab);
        fab.setOnClickListener {

            if(mRepository?.getAllPlaces()!!.size > 10)
            {
                placesViewModel.clearPlacesTable()
            }
            else
            {
                placesViewModel.insert(PlaceObject("Number #${counter++}", "https://picsum.photos/30${(0..9).random()}/200"))
                fragmentPlacesBinding.placesRecycler.smoothScrollToPosition(placesAdapter.itemCount)
            }
        }

        fragmentPlacesBinding.placesRecycler.adapter = placesAdapter
        fragmentPlacesBinding.placesRecycler.layoutManager = LinearLayoutManager(fragmentPlacesBinding.root.context)

        super.onViewCreated(view, savedInstanceState)
    }
}