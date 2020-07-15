package com.example.android.streetworkout.views

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.android.streetworkout.R
import com.example.android.streetworkout.adapters.PlaceAdapter
import com.example.android.streetworkout.common.BaseFragment
import com.example.android.streetworkout.common.MainActivity
import com.example.android.streetworkout.data.model.PlaceObject
import com.example.android.streetworkout.databinding.FragmentFavoritePlacesBinding
import com.example.android.streetworkout.utils.InjectorUtils
import com.example.android.streetworkout.viewmodels.FavoritePlacesViewModel

class FavoritePlacesFragment : BaseFragment() {

    private val favoritePlacesViewModel: FavoritePlacesViewModel by viewModels {
        InjectorUtils.provideFavoritePlacesViewModelFactory(requireActivity())
    }

    private var toolbar: Toolbar? = null
    private lateinit var fragmentFavoritePlacesBinding: FragmentFavoritePlacesBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        fragmentFavoritePlacesBinding = FragmentFavoritePlacesBinding.inflate(inflater, container, false)

        fragmentFavoritePlacesBinding.viewmodel = favoritePlacesViewModel

        fragmentFavoritePlacesBinding.lifecycleOwner = this

        toolbar = fragmentFavoritePlacesBinding.toolbar
        (container?.context as MainActivity).setSupportActionBar(toolbar)

        return fragmentFavoritePlacesBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val placesAdapter =
            PlaceAdapter(object :
                PlaceAdapter.Callback {
                override fun onPlaceClicked(item: PlaceObject) {
                    showBottomSheet()
                }

                override fun onLikeClicked(item: PlaceObject) {
                    removePlaceFromFavorites(item)
                }
            })

        favoritePlacesViewModel.allFavoritePlacesLive.observe(viewLifecycleOwner, Observer { places ->
            places?.let { placesAdapter.setPlaces(it) }
        })

        fragmentFavoritePlacesBinding.placesRecycler.adapter = placesAdapter
        fragmentFavoritePlacesBinding.placesRecycler.layoutManager =
            LinearLayoutManager(fragmentFavoritePlacesBinding.root.context)

        super.onViewCreated(view, savedInstanceState)
    }

    private fun removePlaceFromFavorites(place: PlaceObject) {
        place.changeFavoriteState()
        favoritePlacesViewModel.insertPlace(place)
    }

    fun showBottomSheet() {
        ItemListDialogFragment.newInstance(30)
            .show((requireActivity() as MainActivity).supportFragmentManager, "detail_place_tag")
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

}