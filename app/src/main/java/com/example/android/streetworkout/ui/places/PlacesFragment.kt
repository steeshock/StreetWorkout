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

class PlacesFragment : Fragment() {

    private lateinit var placesViewModel: PlacesViewModel
    private lateinit var fragmentPlacesBinding: FragmentPlacesBinding

    val items = listOf(
        PlaceObject("Александр Пушкин"),
        PlaceObject("Михаил Лермонтов"),
        PlaceObject("Михаил Лермонтов"),
        PlaceObject("Михаил Лермонтов"),
        PlaceObject("Александр Блок"),
        PlaceObject("Николай Некрасов"),
        PlaceObject("Николай Некрасов"),
        PlaceObject("Николай Некрасов"),
        PlaceObject("Фёдор Тютчев"),
        PlaceObject("Сергей Есенин"),
        PlaceObject("Сергей Есенин"),
        PlaceObject("Сергей Есенин"),
        PlaceObject("Сергей Есенин"),
        PlaceObject("Владимир Маяковский")
    )

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

        val placesAdapter = PlaceAdapter(items, object : PlaceAdapter.Callback {
            override fun onItemClicked(item: PlaceObject) {
                Toast.makeText(view.context, item.simpleText, Toast.LENGTH_SHORT).show()
            }
        })

        fragmentPlacesBinding.placesRecycler.adapter = placesAdapter
        fragmentPlacesBinding.placesRecycler.layoutManager = LinearLayoutManager(fragmentPlacesBinding.root.context)

        super.onViewCreated(view, savedInstanceState)

    }
}