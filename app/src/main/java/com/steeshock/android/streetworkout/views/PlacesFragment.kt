package com.steeshock.android.streetworkout.views

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.steeshock.android.streetworkout.R
import com.steeshock.android.streetworkout.adapters.CategoryAdapter
import com.steeshock.android.streetworkout.adapters.PlaceAdapter
import com.steeshock.android.streetworkout.common.App
import com.steeshock.android.streetworkout.common.BaseFragment
import com.steeshock.android.streetworkout.common.MainActivity
import com.steeshock.android.streetworkout.common.appComponent
import com.steeshock.android.streetworkout.data.model.Category
import com.steeshock.android.streetworkout.data.model.Place
import com.steeshock.android.streetworkout.databinding.FragmentPlacesBinding
import com.steeshock.android.streetworkout.viewmodels.CustomPlacesViewModelFactory
import com.steeshock.android.streetworkout.viewmodels.PlacesViewModel
import javax.inject.Inject

class PlacesFragment : BaseFragment() {

    @Inject
    lateinit var factory: CustomPlacesViewModelFactory

    private val placesViewModel: PlacesViewModel by viewModels { factory }

    private lateinit var placesAdapter: PlaceAdapter
    private lateinit var categoriesAdapter: CategoryAdapter

    private var _fragmentPlacesBinding: FragmentPlacesBinding? = null
    private val fragmentPlacesBinding get() = _fragmentPlacesBinding!!

    private var filterList: MutableList<Category> = mutableListOf()
    private var lastSearchString: String? = null

    override fun injectComponent() {
        context?.appComponent?.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _fragmentPlacesBinding = FragmentPlacesBinding.inflate(inflater, container, false)

        fragmentPlacesBinding.lifecycleOwner = this

        (container?.context as MainActivity).setSupportActionBar(_fragmentPlacesBinding?.toolbar)

        return fragmentPlacesBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        placesAdapter =
            PlaceAdapter(object :
                PlaceAdapter.Callback {
                override fun onPlaceClicked(item: Place) {
                    showBottomSheet()
                }

                override fun onLikeClicked(item: Place) {
                    addPlaceToFavorites(item)
                }

                override fun onPlaceLocationClicked(item: Place) {
                    val placeUUID = item.place_uuid

                    val action = PlacesFragmentDirections.actionNavigationPlacesToNavigationMap(placeUUID)
                    view.findNavController().navigate(action)
                }

                override fun setEmptyListState(isEmpty: Boolean) {
                    if (isEmpty) {
                        fragmentPlacesBinding.placesRefresher.visibility = View.GONE
                        fragmentPlacesBinding.emptyResultsViewRefresher.visibility = View.GONE
                        fragmentPlacesBinding.emptyListViewRefresher.visibility = View.VISIBLE
                    }
                    else {
                        fragmentPlacesBinding.placesRefresher.visibility = View.VISIBLE
                        fragmentPlacesBinding.emptyListViewRefresher.visibility = View.GONE
                    }
                }

                override fun setEmptyResultsState(isEmpty: Boolean) {
                    if (isEmpty) {
                        fragmentPlacesBinding.placesRefresher.visibility = View.GONE
                        fragmentPlacesBinding.emptyListViewRefresher.visibility = View.GONE
                        fragmentPlacesBinding.emptyResultsViewRefresher.visibility = View.VISIBLE
                    }
                    else {
                        fragmentPlacesBinding.placesRefresher.visibility = View.VISIBLE
                        fragmentPlacesBinding.emptyResultsViewRefresher.visibility = View.GONE
                    }
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

        fragmentPlacesBinding.placesRecycler.setHasFixedSize(true)
        fragmentPlacesBinding.placesRecycler.adapter = placesAdapter
        fragmentPlacesBinding.placesRecycler.layoutManager =
            LinearLayoutManager(fragmentPlacesBinding.root.context)

        fragmentPlacesBinding.categoriesRecycler.adapter = categoriesAdapter
        fragmentPlacesBinding.categoriesRecycler.layoutManager =
            LinearLayoutManager(fragmentPlacesBinding.root.context, LinearLayoutManager.HORIZONTAL, false)

        setupEmptyViews()

        initData()
    }

    private fun initData() {
        with(placesViewModel) {

            placesLiveData.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                val sortedData = sortDataByCreatedDate(it)
                placesAdapter.setPlaces(sortedData)
                filterData()
            })

            categoriesLiveData.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                categoriesAdapter.setCategories(it)
                updateFilterList()
                filterData()
            })

            isLoading.observe(viewLifecycleOwner, Observer {
                fragmentPlacesBinding.placesRefresher.isRefreshing = it
                fragmentPlacesBinding.emptyListViewRefresher.isRefreshing = it
                fragmentPlacesBinding.emptyResultsViewRefresher.isRefreshing = it
            })

            fragmentPlacesBinding.placesRefresher.setOnRefreshListener {
                fetchDataFromFirebase(placesViewModel)
            }

            fragmentPlacesBinding.emptyListViewRefresher.setOnRefreshListener {
                fetchDataFromFirebase(placesViewModel)
            }

            fragmentPlacesBinding.emptyResultsViewRefresher.setOnRefreshListener {
                fetchDataFromFirebase(placesViewModel)
            }
        }
    }

    private fun fetchDataFromFirebase(placesViewModel: PlacesViewModel) {
        placesViewModel.fetchPlacesFromFirebase()
        placesViewModel.fetchCategoriesFromFirebase()
    }

    private fun addPlaceToFavorites(place: Place) {
        place.changeFavoriteState()
        placesViewModel.updatePlace(place)
    }

    // region Filtering

    private fun filterByCategory(category: Category) {

        category.changeSelectedState()

        if (filterList.contains(category)) filterList.remove(category) else filterList.add(category)

        filterData()

        placesViewModel.updateCategory(category)
    }

    private fun updateFilterList() {
        filterList = categoriesAdapter.getSelectedCategories()
    }

    private fun filterData() {
        
        placesAdapter.filterItemsByCategory(filterList)

        if (!lastSearchString.isNullOrEmpty()){
            filterDataBySearchString(lastSearchString)
        }
    }

    private fun filterDataBySearchString(searchString: String?) {
        lastSearchString = searchString
        placesAdapter.filterItemsBySearchString(lastSearchString)
    }

    // endregion

    // region Sorting

    private fun sortDataByCreatedDate(list: List<Place>): List<Place> {
        return list.sortedBy { i -> i.created }
    }

    // endregion

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
                placesViewModel.clearDatabase()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    // endregion

    private fun showBottomSheet() {
        ItemListDialogFragment.newInstance(30)
            .show((requireActivity() as MainActivity).supportFragmentManager, "detail_place_tag")
    }

    private fun showAddPlaceFragment(it: View) {
        it.findNavController().navigate(R.id.action_navigation_places_to_navigation_add_place)
    }

    private fun setupEmptyViews() {
        fragmentPlacesBinding.emptyListView.image.setImageResource(R.drawable.ic_rage_face)
        fragmentPlacesBinding.emptyListView.title.setText(R.string.empty_places_list_state_message)

        fragmentPlacesBinding.emptyResultsView.image.setImageResource(R.drawable.ic_jackie_face)
        fragmentPlacesBinding.emptyResultsView.title.setText(R.string.empty_state_message)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _fragmentPlacesBinding = null
    }
}