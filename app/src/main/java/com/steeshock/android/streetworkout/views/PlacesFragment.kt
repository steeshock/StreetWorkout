package com.steeshock.android.streetworkout.views

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
import com.steeshock.android.streetworkout.R
import com.steeshock.android.streetworkout.adapters.PlaceAdapter
import com.steeshock.android.streetworkout.common.BaseFragment
import com.steeshock.android.streetworkout.common.MainActivity
import com.steeshock.android.streetworkout.data.model.Place
import com.steeshock.android.streetworkout.databinding.FragmentPlacesBinding
import com.steeshock.android.streetworkout.utils.InjectorUtils
import com.steeshock.android.streetworkout.viewmodels.PlacesViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
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
    private lateinit var fab: FloatingActionButton
    private lateinit var fragmentPlacesBinding: FragmentPlacesBinding

    private var filterList: MutableList<Category> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        fragmentPlacesBinding = FragmentPlacesBinding.inflate(inflater, container, false)

        fragmentPlacesBinding.lifecycleOwner = this

        (container?.context as MainActivity).setSupportActionBar(fragmentPlacesBinding.toolbar)

        fab = fragmentPlacesBinding.fab

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

        fab.setOnClickListener {
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

        placesViewModel.placesLiveData.observe(
            viewLifecycleOwner,
            Observer { state ->
                when (state) {
                    is State.Loading -> showLoading(true)
                    is State.Success -> {
                        if (state.data.isNotEmpty()) {
                            placesAdapter.setPlaces(state.data.toMutableList())
                            filterData()
                            showLoading(false)
                        }
                    }
                    is State.Error -> {
                        showLoading(false)
                    }
                }
            }
        )

        placesViewModel.categoriesLiveData.observe(
            viewLifecycleOwner,
            Observer { state ->
                when (state) {
                    is State.Loading -> showLoading(true)
                    is State.Success -> {
                        if (state.data.isNotEmpty()) {
                            categoriesAdapter.setCategories(state.data.toMutableList())
                            updateFilterList()
                            showLoading(false)
                        }
                    }
                    is State.Error -> {
                        showLoading(false)
                    }
                }
            }
        )

        fragmentPlacesBinding.refresher.setOnRefreshListener {
            getPlaces()
            getCategories()
        }

        if (placesViewModel.placesLiveData.value !is State.Success) {
            getPlaces()
        }

        if (placesViewModel.categoriesLiveData.value !is State.Success) {
            getCategories()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        fragmentPlacesBinding.refresher.isRefreshing = isLoading
    }

    private fun getPlaces(forceUpdate: Boolean  = false) {
        placesViewModel.getPlaces(forceUpdate)
    }

    private fun getCategories(forceUpdate: Boolean  = false) {
        placesViewModel.getCategories(forceUpdate)
    }

    private fun addPlaceToFavorites(place: Place) {
        place.changeFavoriteState()
        placesViewModel.updatePlace(place)
    }


    private fun filterByCategory(category: Category) {

        val newCategory = category.copy(isSelected = true)
        
        if (filterList.contains(newCategory)) filterList.remove(newCategory) else filterList.add(newCategory)
        filterData()

        category.changeSelectedState()
        placesViewModel.updateCategory(category)

    }


    private fun updateFilterList() {
        filterList = categoriesAdapter.getSelectedCategories()
    }

    private fun filterData() {
        placesAdapter.filterItems(filterList)
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
                placesViewModel.removeAllPlacesExceptFavorites(false)
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