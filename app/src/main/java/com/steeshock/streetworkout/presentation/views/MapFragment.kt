package com.steeshock.streetworkout.presentation.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.steeshock.streetworkout.R
import com.steeshock.streetworkout.common.BaseFragment
import com.steeshock.streetworkout.common.appComponent
import com.steeshock.streetworkout.data.model.CustomMarker
import com.steeshock.streetworkout.data.model.PlaceDto
import com.steeshock.streetworkout.databinding.FragmentMapBinding
import com.steeshock.streetworkout.presentation.viewmodels.MapViewModel
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

        val placeId = arguments?.get("placeId")

        if (!movedCameraToInitialPoint && placeId != null && placeId != -1 && placeId is String){

            val place = viewModel.observablePlaces.value?.find { i -> i.placeId == placeId }

            if (place != null){
                
                val placeLocation = LatLng(place.latitude, place.longitude)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(placeLocation, 10f))

                val selectedMarker = markers.find { i -> i.placeId == placeId }
                selectedMarker?.mapMarker?.showInfoWindow()

                movedCameraToInitialPoint = true
            }
        }
    }

    private fun showAllPlaces(placeDtos: List<PlaceDto>) {

        if (placeDtos.isNotEmpty()) {

            lateinit var pin: LatLng
            lateinit var bounds: LatLngBounds
            val pinsPositions = mutableListOf<LatLng>()
            val builder = LatLngBounds.Builder()

            for (place in placeDtos) {
                pin = LatLng(place.latitude, place.longitude)
                val marker = mMap.addMarker(MarkerOptions().position(pin).title(place.title))
                pinsPositions.add(pin)
                place.placeId.let { markers.add(CustomMarker(it, marker)) }
            }

            for (position in pinsPositions) {
                builder.include(position)
            }

            bounds = builder.build()

            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 12))
        }
    }

    override fun onDestroyView() {
       _binding = null
       super.onDestroyView()    
    }
}
