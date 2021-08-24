package com.steeshock.android.streetworkout.views

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.steeshock.android.streetworkout.R
import com.steeshock.android.streetworkout.adapters.PlaceAdapter
import com.steeshock.android.streetworkout.common.BaseFragment
import com.steeshock.android.streetworkout.common.MainActivity
import com.steeshock.android.streetworkout.data.model.Place
import com.steeshock.android.streetworkout.databinding.FragmentFavoritePlacesBinding
import com.steeshock.android.streetworkout.utils.InjectorUtils
import com.steeshock.android.streetworkout.viewmodels.FavoritePlacesViewModel
import kotlinx.android.synthetic.main.v_empty_state.view.*
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class FavoritePlacesFragment : BaseFragment() {

    private val favoritePlacesViewModel: FavoritePlacesViewModel by viewModels {
        InjectorUtils.provideFavoritePlacesViewModelFactory(requireActivity())
    }

    private var _fragmentFavoritePlacesBinding: FragmentFavoritePlacesBinding? = null
    private val fragmentFavoritePlacesBinding get() = _fragmentFavoritePlacesBinding!!

    private lateinit var placesAdapter: PlaceAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _fragmentFavoritePlacesBinding = FragmentFavoritePlacesBinding.inflate(inflater, container, false)

        fragmentFavoritePlacesBinding.viewmodel = favoritePlacesViewModel

        fragmentFavoritePlacesBinding.lifecycleOwner = this

        (container?.context as MainActivity).setSupportActionBar(fragmentFavoritePlacesBinding.toolbar)

        return fragmentFavoritePlacesBinding.root
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
                    favoritePlacesViewModel.removePlaceFromFavorites(item)
                    showRollbackSnack(item)
                }

                override fun onPlaceLocationClicked(item: Place) {
                    val placeUUID = item.place_uuid

                    val action = FavoritePlacesFragmentDirections.actionNavigationFavoritesToNavigationMap(placeUUID)
                    view.findNavController().navigate(action)
                }

                override fun setEmptyListState(isEmpty: Boolean) {
                    if (isEmpty) {
                        fragmentFavoritePlacesBinding.placesRecycler.visibility = View.GONE
                        fragmentFavoritePlacesBinding.emptyResultsView.visibility = View.GONE
                        fragmentFavoritePlacesBinding.emptyListView.visibility = View.VISIBLE
                    }
                    else {
                        fragmentFavoritePlacesBinding.placesRecycler.visibility = View.VISIBLE
                        fragmentFavoritePlacesBinding.emptyListView.visibility = View.GONE
                    }
                }

                override fun setEmptyResultsState(isEmpty: Boolean) {
                    if (isEmpty) {
                        fragmentFavoritePlacesBinding.placesRecycler.visibility = View.GONE
                        fragmentFavoritePlacesBinding.emptyListView.visibility = View.GONE
                        fragmentFavoritePlacesBinding.emptyResultsView.visibility = View.VISIBLE
                    }
                    else {
                        fragmentFavoritePlacesBinding.placesRecycler.visibility = View.VISIBLE
                        fragmentFavoritePlacesBinding.emptyResultsView.visibility = View.GONE
                    }
                }
            })

        favoritePlacesViewModel.favoritePlacesLive.observe(viewLifecycleOwner, Observer {
            val sortedData = sortDataByCreatedDate(it)
            placesAdapter.setPlaces(sortedData)
        })

        fragmentFavoritePlacesBinding.placesRecycler.adapter = placesAdapter
        fragmentFavoritePlacesBinding.placesRecycler.layoutManager =
            LinearLayoutManager(fragmentFavoritePlacesBinding.root.context)

        setupEmptyViews()
    }

    fun showBottomSheet() {
        ItemListDialogFragment.newInstance(30)
            .show((requireActivity() as MainActivity).supportFragmentManager, "detail_place_tag")
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
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun filterDataBySearchString(searchString: String?) {
        placesAdapter.filterItemsBySearchString(searchString)
    }

    private fun sortDataByCreatedDate(list: List<Place>): List<Place> {
        return list.sortedBy { i -> i.created }
    }

    private fun setupEmptyViews() {
        fragmentFavoritePlacesBinding.emptyListView.image.setImageResource(R.drawable.ic_rage_face)
        fragmentFavoritePlacesBinding.emptyListView.title.setText(R.string.empty_favorites_list_state_message)

        fragmentFavoritePlacesBinding.emptyResultsView.image.setImageResource(R.drawable.ic_jackie_face)
        fragmentFavoritePlacesBinding.emptyResultsView.title.setText(R.string.empty_favorites_state_message)
    }

    private fun showRollbackSnack(item: Place) {

        val rollbackSnack = view?.let { Snackbar.make(it, "\"${item.title}\" удалено из избранного", Snackbar.LENGTH_LONG) }

        if (activity is MainActivity) {
            val baseline = (activity as MainActivity).baseline
            rollbackSnack?.anchorView = baseline
        }

        rollbackSnack?.setActionTextColor(ContextCompat.getColor(requireContext(), R.color.snackbarActionTextColor))
        rollbackSnack?.setAction("Вернуть...") {
            favoritePlacesViewModel.returnPlaceToFavorites(item)
        }

        rollbackSnack?.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _fragmentFavoritePlacesBinding = null
    }
}