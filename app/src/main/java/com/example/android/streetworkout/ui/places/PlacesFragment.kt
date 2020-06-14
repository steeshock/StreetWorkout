package com.example.android.streetworkout.ui.places

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.android.streetworkout.BaseFragment
import com.example.android.streetworkout.MainActivity
import com.example.android.streetworkout.R
import com.example.android.streetworkout.databinding.FragmentPlacesBinding
import com.example.android.streetworkout.data.model.PlaceObject
import com.example.android.streetworkout.data.Storage
import com.example.android.streetworkout.utils.CustomFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton

class PlacesFragment : BaseFragment() {

    private lateinit var fab: FloatingActionButton
    private lateinit var placesViewModel: PlacesViewModel
    private lateinit var fragmentPlacesBinding: FragmentPlacesBinding
    private lateinit var places: MutableList<PlaceObject>

    override fun getFactory(): ViewModelProvider.NewInstanceFactory? = CustomFactory(mStorage)

    override fun getViewModel(): ViewModel? = ViewModelProviders.of(this, getFactory()).get(PlacesViewModel::class.java)

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {

        placesViewModel = getViewModel() as PlacesViewModel

        fragmentPlacesBinding  = FragmentPlacesBinding.inflate(inflater, container, false)

        places = mStorage!!.getAllPlaces()

        fragmentPlacesBinding.lifecycleOwner = this

        return fragmentPlacesBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val placesAdapter = PlaceAdapter(places, object : PlaceAdapter.Callback {
            override fun onItemClicked(item: PlaceObject) {
                Toast.makeText(view.context, item.description, Toast.LENGTH_SHORT).show()
            }
        })

        fab = view.findViewById(R.id.fab);
        fab.setOnClickListener {

            if(mStorage?.getAllPlaces()!!.size > 10)
            {
                mStorage?.clearPlacesTable()
                placesAdapter.RefreshAdapter(mStorage?.getAllPlaces()!!)
            }
            else
            {
                val newItem: PlaceObject = PlaceObject("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.", "https://picsum.photos/30${(0..9).random()}/200")
                mStorage?.insertPlace(newItem)
                Toast.makeText(view.context, "In DataBase ${mStorage?.getAllPlaces()!!.size} records", Toast.LENGTH_SHORT).show()
                placesAdapter.AddItem(newItem)
                fragmentPlacesBinding.placesRecycler.smoothScrollToPosition(placesAdapter.itemCount)
            }
        }

        fragmentPlacesBinding.placesRecycler.adapter = placesAdapter
        fragmentPlacesBinding.placesRecycler.layoutManager = LinearLayoutManager(fragmentPlacesBinding.root.context)

        super.onViewCreated(view, savedInstanceState)

    }
}