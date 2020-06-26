package com.example.android.streetworkout.views

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.android.streetworkout.R
import com.example.android.streetworkout.databinding.FragmentFavoritesBinding
import com.example.android.streetworkout.viewmodels.FavoritesViewModel

class FavoritesFragment : Fragment() {

    private lateinit var favoritesViewModel: FavoritesViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        favoritesViewModel = ViewModelProviders.of(this).get(FavoritesViewModel::class.java)

        val binding: FragmentFavoritesBinding =
            FragmentFavoritesBinding.inflate(inflater, container, false)

        binding.viewmodel = favoritesViewModel

        return binding.root
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