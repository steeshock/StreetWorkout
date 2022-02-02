package com.steeshock.android.streetworkout.presentation.views

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.steeshock.android.streetworkout.R
import com.steeshock.android.streetworkout.common.BaseFragment
import com.steeshock.android.streetworkout.common.MainActivity
import com.steeshock.android.streetworkout.common.appComponent
import com.steeshock.android.streetworkout.data.model.Place
import com.steeshock.android.streetworkout.databinding.FragmentPlacesBinding
import com.steeshock.android.streetworkout.presentation.adapters.CategoryAdapter
import com.steeshock.android.streetworkout.presentation.adapters.PlaceAdapter
import com.steeshock.android.streetworkout.presentation.viewmodels.PlacesViewModel
import javax.inject.Inject

class PlacesFragment : BaseFragment() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    private val viewModel: PlacesViewModel by viewModels { factory }

    private lateinit var placesAdapter: PlaceAdapter
    private lateinit var categoriesAdapter: CategoryAdapter

    private var _fragmentPlacesBinding: FragmentPlacesBinding? = null
    private val fragmentPlacesBinding get() = _fragmentPlacesBinding!!

    override fun injectComponent() {
        context?.appComponent?.providePlacesComponent()?.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _fragmentPlacesBinding = FragmentPlacesBinding.inflate(inflater, container, false)
        (container?.context as MainActivity).setSupportActionBar(_fragmentPlacesBinding?.toolbar)
        return fragmentPlacesBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        placesAdapter =
            PlaceAdapter(object :
                PlaceAdapter.Callback {
                override fun onPlaceClicked(place: Place) {
                    showBottomSheet()
                }

                override fun onLikeClicked(place: Place) {
                    viewModel.onLikeClicked(place)
                }

                override fun onPlaceLocationClicked(place: Place) {
                    navigateToMap(place)
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

        categoriesAdapter = CategoryAdapter {
            viewModel.onFilterByCategory(it)
        }

        fragmentPlacesBinding.fab.setOnClickListener {
            showAddPlaceFragment(it)
        }

        fragmentPlacesBinding.placesRecycler.setHasFixedSize(true)
        fragmentPlacesBinding.placesRecycler.adapter = placesAdapter

        fragmentPlacesBinding.categoriesRecycler.adapter = categoriesAdapter
        fragmentPlacesBinding.categoriesRecycler.layoutManager =
            LinearLayoutManager(fragmentPlacesBinding.root.context, LinearLayoutManager.HORIZONTAL, false)

        setupEmptyViews()

        initData()
    }

    private fun navigateToMap(place: Place) {
        val placeUUID = place.place_uuid
        val action = PlacesFragmentDirections.actionNavigationPlacesToNavigationMap(placeUUID)
        this.findNavController().navigate(action)
    }

    private fun initData() {
        with(viewModel) {

            observablePlaces.observe(viewLifecycleOwner) {
                placesAdapter.setPlaces(it)
            }

            observableCategories.observe(viewLifecycleOwner) {
                categoriesAdapter.setCategories(it)
            }

            viewState.observe(viewLifecycleOwner) {
                fragmentPlacesBinding.placesRefresher.isRefreshing = it.isLoading
                fragmentPlacesBinding.emptyListViewRefresher.isRefreshing = it.isLoading
                fragmentPlacesBinding.emptyResultsViewRefresher.isRefreshing = it.isLoading
            }

            fragmentPlacesBinding.placesRefresher.setOnRefreshListener {
                fetchData(viewModel)
            }

            fragmentPlacesBinding.emptyListViewRefresher.setOnRefreshListener {
                fetchData(viewModel)
            }

            fragmentPlacesBinding.emptyResultsViewRefresher.setOnRefreshListener {
                fetchData(viewModel)
            }
        }
    }

    private fun fetchData(placesViewModel: PlacesViewModel) {
        placesViewModel.fetchPlaces()
        placesViewModel.fetchCategories()
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
                viewModel.filterDataBySearchString(s)
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
                viewModel.clearDatabase()
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