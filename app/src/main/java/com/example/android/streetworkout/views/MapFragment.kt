package com.example.android.streetworkout.views

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.android.streetworkout.R
import com.example.android.streetworkout.common.BaseFragment
import com.example.android.streetworkout.common.MainActivity
import com.example.android.streetworkout.databinding.FragmentMapBinding
import com.example.android.streetworkout.databinding.FragmentPlacesBinding
import com.example.android.streetworkout.viewmodels.MapViewModel

class MapFragment : BaseFragment() {

    private lateinit var mapViewModel: MapViewModel

    private var toolbar: Toolbar? = null
    private lateinit var fragmentMapBinding: FragmentMapBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        fragmentMapBinding = FragmentMapBinding.inflate(inflater, container, false)

        mapViewModel = ViewModelProviders.of(this).get(MapViewModel::class.java)

        fragmentMapBinding = FragmentMapBinding.inflate(inflater, container, false)

        fragmentMapBinding.viewmodel = mapViewModel

        toolbar = fragmentMapBinding.toolbar
        (container?.context as MainActivity).setSupportActionBar(toolbar)

        return fragmentMapBinding.root
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
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}