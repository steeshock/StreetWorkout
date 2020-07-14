package com.example.android.streetworkout.views

import android.os.Bundle
import android.os.Handler
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.android.streetworkout.R
import com.example.android.streetworkout.adapters.PlaceAdapter
import com.example.android.streetworkout.common.BaseFragment
import com.example.android.streetworkout.common.MainActivity
import com.example.android.streetworkout.data.model.PlaceObject
import com.example.android.streetworkout.databinding.FragmentPlacesBinding
import com.example.android.streetworkout.utils.InjectorUtils
import com.example.android.streetworkout.viewmodels.PlacesViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class PlacesFragment : BaseFragment(), SwipeRefreshLayout.OnRefreshListener {

    private val placesViewModel: PlacesViewModel by viewModels {
        InjectorUtils.providePlacesViewModelFactory(requireActivity())
    }

    private var toolbar: Toolbar? = null
    private lateinit var fab: FloatingActionButton
    private lateinit var fragmentPlacesBinding: FragmentPlacesBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        fragmentPlacesBinding = FragmentPlacesBinding.inflate(inflater, container, false)

        fragmentPlacesBinding.refresher.setOnRefreshListener(this)

        fragmentPlacesBinding.lifecycleOwner = this

        toolbar = fragmentPlacesBinding.toolbar
        (container?.context as MainActivity).setSupportActionBar(toolbar)

        fab = fragmentPlacesBinding.fab

        return fragmentPlacesBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val placesAdapter =
            PlaceAdapter(object :
                PlaceAdapter.Callback {
                override fun onPlaceClicked(item: PlaceObject) {
                    showBottomSheet()
                }

                override fun onLikeClicked(item: PlaceObject) {
                    Toast.makeText(view.context, item.address, Toast.LENGTH_SHORT).show()
                }
            })

        placesViewModel.allPlacesLive.observe(viewLifecycleOwner, Observer { places ->
            places?.let { placesAdapter.setPlaces(it) }
        })

        fab.setOnClickListener {
            showAddPlaceFragment(it)
        }

        fragmentPlacesBinding.placesRecycler.adapter = placesAdapter
        fragmentPlacesBinding.placesRecycler.layoutManager =
            LinearLayoutManager(fragmentPlacesBinding.root.context)

        super.onViewCreated(view, savedInstanceState)
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
                placesViewModel.clearPlacesTable()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun showBottomSheet() {
        ItemListDialogFragment.newInstance(30)
            .show((requireActivity() as MainActivity).supportFragmentManager, "detail_place_tag")
    }

    private fun showAddPlaceFragment(it: View) {
        it.findNavController().navigate(R.id.action_navigation_places_to_navigation_add_place)
    }

    override fun onRefresh() {

        //ToDo make update from API

        fragmentPlacesBinding.refresher.isRefreshing = true

        Handler().postDelayed({
            //(fragmentPlacesBinding.placesRecycler.adapter as PlaceAdapter).sortItems()

            placesViewModel.updateProjects()

            fragmentPlacesBinding.refresher.isRefreshing = false
        }, 2000)
    }
}