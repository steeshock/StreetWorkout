package com.steeshock.android.streetworkout.presentation.views

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.steeshock.android.streetworkout.R
import com.steeshock.android.streetworkout.common.BaseFragment
import com.steeshock.android.streetworkout.common.appComponent
import com.steeshock.android.streetworkout.data.model.CustomMarker
import com.steeshock.android.streetworkout.data.model.Place
import com.steeshock.android.streetworkout.databinding.FragmentMapBinding
import com.steeshock.android.streetworkout.presentation.viewmodels.MapViewModel
import javax.inject.Inject

class MapFragment : BaseFragment(), OnMapReadyCallback {

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    private val viewModel: MapViewModel by viewModels { factory }

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    private lateinit var mMap: GoogleMap
    private var markers : MutableList<CustomMarker> = mutableListOf()

    private var movedCameraToInitialPoint = false

    override fun injectComponent() {
        context?.appComponent?.provideMapComponent()?.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        (container?.context as MainActivity).setSupportActionBar(binding.toolbar)
        mapFragment?.getMapAsync(this)
        return binding.root
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        viewModel.observablePlaces.observe(viewLifecycleOwner) { places ->
            places?.let { showAllPlaces(it) }
            moveToPointLocation()
        }
    }

    private fun moveToPointLocation() {

        val placeUUID = arguments?.get("place_uuid")

        if (!movedCameraToInitialPoint && placeUUID != null && placeUUID != -1 && placeUUID is String){

            val place = viewModel.observablePlaces.value?.find { i -> i.place_id == placeUUID }

            if (place != null){
                
                val placeLocation = LatLng(place.latitude, place.longitude)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(placeLocation, 10f))

                val selectedMarker = markers.find { i -> i.place_uuid == placeUUID }
                selectedMarker?.mapMarker?.showInfoWindow()

                movedCameraToInitialPoint = true
            }
        }
    }

    private fun showAllPlaces(places: List<Place>) {

        if (places.isNotEmpty()) {

            lateinit var pin: LatLng
            lateinit var bounds: LatLngBounds
            val pinsPositions = mutableListOf<LatLng>()
            val builder = LatLngBounds.Builder()

            for (place in places) {
                pin = LatLng(place.latitude, place.longitude)
                val marker = mMap.addMarker(MarkerOptions().position(pin).title(place.title))
                pinsPositions.add(pin)
                place.place_id.let { markers.add(CustomMarker(it, marker)) }
            }

            for (position in pinsPositions) {
                builder.include(position)
            }

            bounds = builder.build()

            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 12))
        }
    }

    // region Menu
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

        inflater.inflate(R.menu.activity_menu, menu)

        val myActionMenuItem = menu.findItem(R.id.action_search)

        val searchView = myActionMenuItem.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                if (!searchView.isIconified) {
                    searchView.isIconified = true
                }
                myActionMenuItem.collapseActionView()
                return false
            }

            override fun onQueryTextChange(s: String?): Boolean {
                return false
            }
        })

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_search -> {
                true
            }
            R.id.action_map -> {
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    // endregion

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
