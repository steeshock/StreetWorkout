package com.steeshock.streetworkout.presentation.views

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.steeshock.streetworkout.R
import com.steeshock.streetworkout.common.BaseFragment
import com.steeshock.streetworkout.common.appComponent
import com.steeshock.streetworkout.data.model.Place
import com.steeshock.streetworkout.databinding.FragmentFavoritePlacesBinding
import com.steeshock.streetworkout.extensions.gone
import com.steeshock.streetworkout.extensions.visible
import com.steeshock.streetworkout.presentation.adapters.PlaceAdapter
import com.steeshock.streetworkout.presentation.viewStates.EmptyViewState.*
import com.steeshock.streetworkout.presentation.viewStates.places.PlacesViewEvent
import com.steeshock.streetworkout.presentation.viewStates.places.PlacesViewState
import com.steeshock.streetworkout.presentation.viewmodels.FavoritePlacesViewModel
import javax.inject.Inject

class FavoritePlacesFragment : BaseFragment() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    private val viewModel: FavoritePlacesViewModel by activityViewModels { factory }

    private var _binding: FragmentFavoritePlacesBinding? = null
    private val binding get() = _binding!!

    private lateinit var placesAdapter: PlaceAdapter

    override fun injectComponent() {
        context?.appComponent?.provideFavoritePlacesComponent()?.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentFavoritePlacesBinding.inflate(inflater, container, false)
        (container?.context as MainActivity).setSupportActionBar(binding.toolbar)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        placesAdapter =
            PlaceAdapter(object :
                PlaceAdapter.Callback {
                override fun onPlaceClicked(place: Place) {}

                override fun onLikeClicked(place: Place) {
                    viewModel.onFavoriteStateChanged(place)
                    showRollbackSnack(place)
                }

                override fun onPlaceLocationClicked(place: Place) {
                    view.findNavController().navigate(
                        FavoritePlacesFragmentDirections.actionNavigationFavoritesToNavigationMap(
                            place.placeId)
                    )
                }
            })

        binding.placesRecycler.adapter = placesAdapter
        initData()
    }

    private fun initData() {
        with(viewModel) {
            observablePlaces.observe(viewLifecycleOwner) {
                placesAdapter.setPlaces(it)
            }

            viewState.observe(viewLifecycleOwner) {
                renderViewState(it)
            }

            viewEvent.observe(viewLifecycleOwner) {
                renderViewEvent(it)
            }

            binding.refresher.setOnRefreshListener {
                updateFavoritePlaces()
            }
        }
    }

    private fun renderViewState(viewState: PlacesViewState) {
        binding.refresher.isRefreshing = viewState.isLoading
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
        if (viewEvent == PlacesViewEvent.NoInternetConnection) {
            view.showNoInternetSnackbar()
        }
    }

    // region Menu
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.favorites_menu, menu)

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
            else -> super.onOptionsItemSelected(item)
        }
    }
    // endregion

    private fun showRollbackSnack(item: Place) {
        view.showSnackbar(
            message = "\"${item.title}\" ${resources.getString(R.string.place_removed)}",
            action = { viewModel.returnPlaceToFavorites(item) },
            actionText = getString(R.string.rollback_place),
        )
    }

    override fun onDestroyView() {
        viewModel.resetSearchFilter()
        _binding = null
        super.onDestroyView()
    }
}
