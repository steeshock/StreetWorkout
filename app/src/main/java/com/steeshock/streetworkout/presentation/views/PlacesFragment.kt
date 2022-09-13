package com.steeshock.streetworkout.presentation.views

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.steeshock.streetworkout.R
import com.steeshock.streetworkout.common.BaseFragment
import com.steeshock.streetworkout.common.appComponent
import com.steeshock.streetworkout.databinding.FragmentPlacesBinding
import com.steeshock.streetworkout.domain.entity.Place
import com.steeshock.streetworkout.domain.entity.enums.SignPurpose.SIGN_IN
import com.steeshock.streetworkout.extensions.gone
import com.steeshock.streetworkout.extensions.showAlertDialog
import com.steeshock.streetworkout.extensions.visible
import com.steeshock.streetworkout.presentation.adapters.CategoryAdapter
import com.steeshock.streetworkout.presentation.adapters.PlaceAdapter
import com.steeshock.streetworkout.presentation.viewStates.EmptyViewState.*
import com.steeshock.streetworkout.presentation.viewStates.places.PlacesViewEvent
import com.steeshock.streetworkout.presentation.viewStates.places.PlacesViewEvent.*
import com.steeshock.streetworkout.presentation.viewStates.places.PlacesViewState
import com.steeshock.streetworkout.presentation.viewmodels.PlacesViewModel
import javax.inject.Inject

class PlacesFragment : BaseFragment() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    private val viewModel: PlacesViewModel by viewModels { factory }

    private lateinit var placesAdapter: PlaceAdapter
    private lateinit var categoriesAdapter: CategoryAdapter

    private var _binding: FragmentPlacesBinding? = null
    private val binding get() = _binding!!

    override fun injectComponent() {
        context?.appComponent?.providePlacesComponent()?.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentPlacesBinding.inflate(inflater, container, false)
        (container?.context as MainActivity).setSupportActionBar(binding.toolbar)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initFab()
        initPlacesRecycler()
        initCategoriesRecycler()
        initData()
    }

    private fun initFab() {
        binding.fab.setOnClickListener {
            viewModel.onAddNewPlaceClicked()
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initPlacesRecycler() {
        placesAdapter = PlaceAdapter(object : PlaceAdapter.Callback {
            override fun onPlaceClicked(place: Place) {}

            override fun onLikeClicked(place: Place) {
                viewModel.onLikeClicked(place)
            }

            override fun onPlaceLocationClicked(place: Place) {
                navigateToMap(place)
            }

            override fun onPlaceDeleteClicked(place: Place) {
                viewModel.onPlaceDeleteClicked(place)
            }
        })
        val dividerItemDecoration = DividerItemDecoration(requireContext(), RecyclerView.VERTICAL)
        dividerItemDecoration.setDrawable(resources.getDrawable(R.drawable.list_item_divider, null))
        binding.placesRecycler.addItemDecoration(dividerItemDecoration)
        binding.placesRecycler.setHasFixedSize(true)
        binding.placesRecycler.adapter = placesAdapter
    }

    private fun initCategoriesRecycler() {
        categoriesAdapter = CategoryAdapter {
            viewModel.onFilterByCategory(it)
        }
        binding.categoriesRecycler.layoutManager =
            LinearLayoutManager(binding.root.context, LinearLayoutManager.HORIZONTAL, false)
        binding.categoriesRecycler.adapter = categoriesAdapter
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
                renderViewState(it)
            }

            viewEvent.observe(viewLifecycleOwner) {
                renderViewEvent(it)
            }

            binding.refresher.setOnRefreshListener {
                fetchData()
            }
        }
    }

    private fun renderViewState(viewState: PlacesViewState) {
        binding.refresher.isRefreshing = viewState.isPlacesLoading
        setFullscreenLoader(viewState.showFullscreenLoader)
        when (viewState.emptyState) {
            EMPTY_PLACES -> {
                binding.placesRecycler.gone()
                binding.emptyList.visible()
                binding.emptyResults.gone()
            }
            EMPTY_SEARCH_RESULTS -> {
                binding.placesRecycler.gone()
                binding.emptyList.gone()
                binding.emptyResults.visible()
            }
            NOT_EMPTY -> {
                binding.placesRecycler.visible()
                binding.emptyList.gone()
                binding.emptyResults.gone()
            }
        }
    }

    private fun renderViewEvent(viewEvent: PlacesViewEvent) {
        when (viewEvent) {
            ShowAddPlaceFragment -> {
                showAddPlaceFragment()
            }
            ShowAddPlaceAuthAlert -> {
                showAddPlaceAuthAlert()
            }
            ShowAddToFavoritesAuthAlert -> {
                showAddToFavoritesAuthAlert()
            }
            NoInternetConnection -> {
                view.showNoInternetSnackbar()
            }
            is ShowDeletePlaceAlert -> {
                showDeletePlaceAlert(viewEvent.place)
            }
            ShowDeletePlaceSuccess -> {
                showDeletePlaceSuccess()
            }
        }
    }

    private fun showAddPlaceFragment() {
        findNavController().navigate(R.id.action_navigation_places_to_navigation_add_place)
    }

    private fun showAddPlaceAuthAlert() {
        requireActivity().showAlertDialog(
            title = getString(R.string.attention_title),
            message = getString(R.string.sign_in_dialog_message),
            positiveText = getString(R.string.sign_in_button_title),
            negativeText = getString(R.string.cancel_item),
            onPositiveAction = { navigateToProfile() },
        )
    }

    private fun showAddToFavoritesAuthAlert() {
        view.showSnackbar(
            message = getString(R.string.sign_in_snackbar_message),
            actionText = getString(R.string.login_title),
            action = { navigateToProfile() }
        )
    }

    private fun showDeletePlaceAlert(place: Place) {
        requireActivity().showAlertDialog(
            title = getString(R.string.attention_title),
            message = getString(R.string.delete_place_dialog_message),
            positiveText = getString(R.string.ok_item),
            negativeText = getString(R.string.cancel_item),
            onPositiveAction = { viewModel.deletePlace(place) },
        )
    }

    private fun showDeletePlaceSuccess() {
        view.showSnackbar(getString(R.string.delete_place_success_message))
    }

    private fun navigateToMap(place: Place) {
        findNavController().navigate(
            PlacesFragmentDirections.actionNavigationPlacesToNavigationMap(place.placeId)
        )
    }

    private fun navigateToProfile() {
        try {
            findNavController().navigate(
                PlacesFragmentDirections.actionNavigationPlacesToNavigationProfile(SIGN_IN.toString())
            )
        } catch (e: Exception) {}
    }

    // region Menu
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.places_menu, menu)

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
            R.id.action_sort -> {
                viewModel.clearDatabase()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    // endregion

    override fun onDestroyView() {
        viewModel.resetSearchFilter()
        _binding = null
        super.onDestroyView()
    }
}
