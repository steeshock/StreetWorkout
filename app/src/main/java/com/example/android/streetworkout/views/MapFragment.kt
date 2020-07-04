package com.example.android.streetworkout.views

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProviders
import com.example.android.streetworkout.R
import com.example.android.streetworkout.common.BaseFragment
import com.example.android.streetworkout.common.MainActivity
import com.example.android.streetworkout.databinding.FragmentMapBinding
import com.example.android.streetworkout.utils.InjectorUtils
import com.example.android.streetworkout.viewmodels.MapViewModel
import com.example.android.streetworkout.viewmodels.PlacesViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapFragment : BaseFragment(), OnMapReadyCallback {
    private val mapViewModel: MapViewModel by viewModels {
        InjectorUtils.provideMapViewModelFactory(requireActivity())
    }

    private lateinit var mMap: GoogleMap

    private var toolbar: Toolbar? = null
    private lateinit var fragmentMapBinding: FragmentMapBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        fragmentMapBinding = FragmentMapBinding.inflate(inflater, container, false)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?

        fragmentMapBinding.viewmodel = mapViewModel

        toolbar = fragmentMapBinding.toolbar
        (container?.context as MainActivity).setSupportActionBar(toolbar)

        mapFragment?.getMapAsync(this)

        return fragmentMapBinding.root
    }

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
                //Здесь слушаем именение текста
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
                showAllPlaces()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showAllPlaces() {
        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
    }

    fun convertStringToLatLng(position: String): LatLng {
        val (lat, lng) = position.split(",").map { it.toDouble() }
        return LatLng(lat, lng)
    }
}
