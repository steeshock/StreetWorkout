package com.steeshock.android.streetworkout.presentation.views

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.steeshock.android.streetworkout.R
import com.steeshock.android.streetworkout.common.BaseFragment
import com.steeshock.android.streetworkout.common.appComponent
import com.steeshock.android.streetworkout.data.model.Place
import com.steeshock.android.streetworkout.databinding.FragmentFavoritePlacesBinding
import com.steeshock.android.streetworkout.presentation.adapters.PlaceAdapter
import com.steeshock.android.streetworkout.presentation.viewStates.EmptyViewState.*
import com.steeshock.android.streetworkout.presentation.viewStates.PlacesViewState
import com.steeshock.android.streetworkout.presentation.viewmodels.FavoritePlacesViewModel
import com.steeshock.android.streetworkout.utils.extensions.gone
import com.steeshock.android.streetworkout.utils.extensions.visible
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
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritePlacesBinding.inflate(inflater, container, false)
        (container?.context as HomeActivity).setSupportActionBar(binding.toolbar)
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
                        FavoritePlacesFragmentDirections.actionNavigationFavoritesToNavigationMap(place.place_uuid)
                    )
                }
            })

        binding.placesRecycler.adapter = placesAdapter
        setupEmptyViews()
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
        }
    }

    private fun renderViewState(viewState: PlacesViewState?) {
        when (viewState?.emptyState) {
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
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    // endregion

    private fun showRollbackSnack(item: Place) {

        val rollbackSnack = view?.let {
            Snackbar.make(it,
                "\"${item.title}\" ${resources.getString(R.string.place_removed)}",
                Snackbar.LENGTH_LONG
            )
        }

        if (activity is HomeActivity) {
            val baseline = (activity as HomeActivity).baseline
            rollbackSnack?.anchorView = baseline
        }

        rollbackSnack?.setActionTextColor(ContextCompat.getColor(requireContext(), R.color.snackbarActionTextColor))
        rollbackSnack?.setAction(R.string.rollback_place) {
            viewModel.returnPlaceToFavorites(item)
        }

        rollbackSnack?.animationMode = BaseTransientBottomBar.ANIMATION_MODE_FADE

        rollbackSnack?.show()
    }

    private fun setupEmptyViews() {
        binding.emptyList.image.setImageResource(R.drawable.ic_rage_face)
        binding.emptyList.title.setText(R.string.empty_favorites_list_state_message)

        binding.emptyResults.image.setImageResource(R.drawable.ic_jackie_face)
        binding.emptyResults.title.setText(R.string.empty_favorites_state_message)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
