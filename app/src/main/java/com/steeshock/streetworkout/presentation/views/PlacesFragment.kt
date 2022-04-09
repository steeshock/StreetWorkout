package com.steeshock.streetworkout.presentation.views

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.steeshock.streetworkout.R
import com.steeshock.streetworkout.common.BaseFragment
import com.steeshock.streetworkout.common.appComponent
import com.steeshock.streetworkout.data.model.Place
import com.steeshock.streetworkout.databinding.FragmentPlacesBinding
import com.steeshock.streetworkout.presentation.adapters.CategoryAdapter
import com.steeshock.streetworkout.presentation.adapters.PlaceAdapter
import com.steeshock.streetworkout.presentation.viewStates.EmptyViewState.*
import com.steeshock.streetworkout.presentation.viewStates.PlacesViewEvent
import com.steeshock.streetworkout.presentation.viewStates.PlacesViewEvent.ShowAddPlaceFragment
import com.steeshock.streetworkout.presentation.viewStates.PlacesViewEvent.ShowAuthenticationAlert
import com.steeshock.streetworkout.presentation.viewStates.PlacesViewState
import com.steeshock.streetworkout.presentation.viewmodels.PlacesViewModel
import com.steeshock.streetworkout.presentation.viewmodels.ProfileViewModel.SignPurpose.SIGN_IN
import com.steeshock.streetworkout.utils.extensions.gone
import com.steeshock.streetworkout.utils.extensions.visible
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
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlacesBinding.inflate(inflater, container, false)
        (container?.context as MainActivity).setSupportActionBar(_binding?.toolbar)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        placesAdapter = PlaceAdapter(object : PlaceAdapter.Callback {
                override fun onPlaceClicked(place: Place) {}

                override fun onLikeClicked(place: Place) {
                    viewModel.onLikeClicked(place)
                }

                override fun onPlaceLocationClicked(place: Place) {
                    navigateToMap(place)
                }
            })

        categoriesAdapter = CategoryAdapter {
            viewModel.onFilterByCategory(it)
        }

        binding.fab.setOnClickListener {
            viewModel.onAddNewPlaceClicked()
        }

        binding.placesRecycler.setHasFixedSize(true)
        binding.placesRecycler.adapter = placesAdapter

        binding.categoriesRecycler.adapter = categoriesAdapter
        binding.categoriesRecycler.layoutManager =
            LinearLayoutManager(binding.root.context, LinearLayoutManager.HORIZONTAL, false)

        setupEmptyViews()
        initData()
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
                fetchData(viewModel)
            }
        }
    }

    private fun renderViewState(viewState: PlacesViewState) {
        binding.refresher.isRefreshing = viewState.isLoading

        when (viewState.emptyState) {
            EMPTY_PLACES -> {
                binding.placesRecycler.gone()
                binding.emptyList.mainLayout.visible()
                binding.emptyResults.mainLayout.gone()
            }
            EMPTY_SEARCH_RESULTS -> {
                binding.placesRecycler.gone()
                binding.emptyList.mainLayout.gone()
                binding.emptyResults.mainLayout.visible()
            }
            NOT_EMPTY -> {
                binding.placesRecycler.visible()
                binding.emptyList.mainLayout.gone()
                binding.emptyResults.mainLayout.gone()
            }
        }
    }

    private fun renderViewEvent(viewEvent: PlacesViewEvent) {
        when(viewEvent) {
            ShowAddPlaceFragment -> {
                showAddPlaceFragment()
            }
            ShowAuthenticationAlert -> {
                showAuthenticationAlert()
            }
        }
    }

    private fun fetchData(placesViewModel: PlacesViewModel) {
        placesViewModel.fetchPlaces()
        placesViewModel.fetchCategories()
    }

    private fun showAddPlaceFragment() {
        findNavController().navigate(R.id.action_navigation_places_to_navigation_add_place)
    }

    private fun showAuthenticationAlert() {
        showModalDialog(
            title = getString(R.string.clear_fields_alert),
            message = getString(R.string.sign_in_alert_dialog_message),
            positiveText = getString(R.string.sign_in_button_title),
            negativeText = getString(R.string.cancel_item),
            onPositiveAction = { navigateToProfile() },
        )
    }

    private fun navigateToMap(place: Place) {
        findNavController().navigate(
            PlacesFragmentDirections.actionNavigationPlacesToNavigationMap(place.place_id)
        )
    }

    private fun navigateToProfile() {
        findNavController().navigate(
            PlacesFragmentDirections.actionNavigationPlacesToNavigationProfile(SIGN_IN.toString())
        )
    }

    private fun setupEmptyViews() {
        binding.emptyList.image.setImageResource(R.drawable.ic_rage_face)
        binding.emptyList.title.setText(R.string.empty_places_list_state_message)

        binding.emptyResults.image.setImageResource(R.drawable.ic_jackie_face)
        binding.emptyResults.title.setText(R.string.empty_state_message)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
