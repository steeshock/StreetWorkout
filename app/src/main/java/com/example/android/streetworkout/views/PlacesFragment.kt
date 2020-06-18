package com.example.android.streetworkout.views

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.android.streetworkout.common.BaseFragment
import com.example.android.streetworkout.R
import com.example.android.streetworkout.data.model.PlaceObject
import com.example.android.streetworkout.databinding.FragmentPlacesBinding
import com.example.android.streetworkout.adapters.PlaceAdapter
import com.example.android.streetworkout.common.MainActivity
import com.example.android.streetworkout.utils.InjectorUtils
import com.example.android.streetworkout.viewmodels.PlacesViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.activity_main.*

class PlacesFragment : BaseFragment() {

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
                override fun onItemClicked(item: PlaceObject) {
                    showBottomSheet()
                }
            })

        placesViewModel.allPlacesLive.observe(viewLifecycleOwner, Observer { places ->
            places?.let { placesAdapter.setPlaces(it) }
        })

        fab.setOnClickListener {

            showAddPlaceFragment()
        }

        fragmentPlacesBinding.placesRecycler.adapter = placesAdapter
        fragmentPlacesBinding.placesRecycler.layoutManager =
            LinearLayoutManager(fragmentPlacesBinding.root.context)

        super.onViewCreated(view, savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.activity_menu, menu)

        val myActionMenuItem = menu.findItem(R.id.action_search)

        var searchView = myActionMenuItem.actionView as SearchView
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
        ItemListDialogFragment.newInstance(30).show((requireActivity() as MainActivity).supportFragmentManager, "detail_place_tag")
    }

    private fun showAddPlaceFragment() {
        AddPlaceFragment.newInstance().show((requireActivity() as MainActivity).supportFragmentManager, "add_place_tag")
    }
}