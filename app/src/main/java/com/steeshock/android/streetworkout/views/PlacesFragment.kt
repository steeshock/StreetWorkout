package com.steeshock.android.streetworkout.views

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.steeshock.android.streetworkout.R
import com.steeshock.android.streetworkout.adapters.PlaceAdapter
import com.steeshock.android.streetworkout.common.BaseFragment
import com.steeshock.android.streetworkout.common.MainActivity
import com.steeshock.android.streetworkout.data.model.Place
import com.steeshock.android.streetworkout.databinding.FragmentPlacesBinding
import com.steeshock.android.streetworkout.utils.InjectorUtils
import com.steeshock.android.streetworkout.viewmodels.PlacesViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.steeshock.android.streetworkout.adapters.CategoryAdapter
import com.steeshock.android.streetworkout.data.model.Category
import com.steeshock.android.streetworkout.data.model.State
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class PlacesFragment : BaseFragment(){

    private val placesViewModel: PlacesViewModel by viewModels {
        InjectorUtils.providePlacesViewModelFactory(requireActivity())
    }

    private lateinit var placesAdapter: PlaceAdapter
    private lateinit var categoriesAdapter: CategoryAdapter
    private lateinit var fragmentPlacesBinding: FragmentPlacesBinding

    private var filterList: MutableList<Category> = mutableListOf()
    private var lastSearchString: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        fragmentPlacesBinding = FragmentPlacesBinding.inflate(inflater, container, false)

        fragmentPlacesBinding.lifecycleOwner = this

        (container?.context as MainActivity).setSupportActionBar(fragmentPlacesBinding.toolbar)

        return fragmentPlacesBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        placesAdapter =
            PlaceAdapter(object :
                PlaceAdapter.Callback {
                override fun onPlaceClicked(item: Place) {
                    showBottomSheet()
                }

                override fun onLikeClicked(item: Place) {
                    addPlaceToFavorites(item)
                }
            })

        categoriesAdapter =
            CategoryAdapter(object :
                CategoryAdapter.Callback {
                override fun onClicked(item: Category) {
                    filterByCategory(item)
                }
            })

        fragmentPlacesBinding.fab.setOnClickListener {
            showAddPlaceFragment(it)
        }

        fragmentPlacesBinding.placesRecycler.adapter = placesAdapter
        fragmentPlacesBinding.placesRecycler.layoutManager =
            LinearLayoutManager(fragmentPlacesBinding.root.context)

        fragmentPlacesBinding.categoriesRecycler.adapter = categoriesAdapter
        fragmentPlacesBinding.categoriesRecycler.layoutManager =
            LinearLayoutManager(fragmentPlacesBinding.root.context, LinearLayoutManager.HORIZONTAL, false)

        initData()

        super.onViewCreated(view, savedInstanceState)
    }

    private fun initData() {
        with(placesViewModel) {

            placesLiveData.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                placesAdapter.setPlaces(it)
                filterDataByFilterList()
            })

            categoriesLiveData.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                categoriesAdapter.setCategories(it)
                updateFilterList()
                filterDataByFilterList()
            })

            isLoading.observe(viewLifecycleOwner, Observer {
                fragmentPlacesBinding.refresher.isRefreshing = it
            })

            fragmentPlacesBinding.refresher.setOnRefreshListener {
                updatePlacesFromFirebase()
                updateCategoriesFromFirebase()
            }
        }
    }

    private fun addPlaceToFavorites(place: Place) {
        place.changeFavoriteState()
        placesViewModel.updatePlace(place)
    }

    private fun filterByCategory(category: Category) {

        category.changeSelectedState()

        if (filterList.contains(category)) filterList.remove(category) else filterList.add(category)
        filterDataByFilterList()

        placesViewModel.updateCategory(category)

    }

    private fun updateFilterList() {
        filterList = categoriesAdapter.getSelectedCategories()
    }

    private fun filterDataByFilterList() {
        placesAdapter.filterItemsByFilterList(filterList)

        if (!lastSearchString.isNullOrEmpty()){
            filterDataBySearchString(lastSearchString)
        }
    }

    private fun filterDataBySearchString(searchString: String?) {
        lastSearchString = searchString
        placesAdapter.filterItemsBySearchString(searchString)
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
                filterDataBySearchString(s)
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
                //placesViewModel.removeAllPlacesExceptFavorites(false)
                //placesViewModel.removeAllPlaces()
                placesViewModel.clearDatabase()
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
}