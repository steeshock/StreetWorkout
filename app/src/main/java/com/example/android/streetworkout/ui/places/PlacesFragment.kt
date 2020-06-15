package com.example.android.streetworkout.ui.places

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.android.streetworkout.BaseFragment
import com.example.android.streetworkout.MainActivity
import com.example.android.streetworkout.R
import com.example.android.streetworkout.data.model.PlaceObject
import com.example.android.streetworkout.databinding.FragmentPlacesBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton

class PlacesFragment : BaseFragment() {

    private var toolbar: Toolbar? = null

    private var counter:Int = 0

    private lateinit var fab: FloatingActionButton

    private lateinit var placesViewModel: PlacesViewModel
    private lateinit var fragmentPlacesBinding: FragmentPlacesBinding

    override fun getViewModel(): ViewModel? = ViewModelProvider(this).get(PlacesViewModel::class.java)

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {

        placesViewModel = getViewModel() as PlacesViewModel

        fragmentPlacesBinding  = FragmentPlacesBinding.inflate(inflater, container, false)

        fragmentPlacesBinding.lifecycleOwner = this

        toolbar = fragmentPlacesBinding.toolbar
        if (container != null) {
            (container.context as MainActivity).setSupportActionBar(toolbar)
        }

        return fragmentPlacesBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val placesAdapter = PlaceAdapter(object : PlaceAdapter.Callback {
            override fun onItemClicked(item: PlaceObject) {
                Toast.makeText(view.context, item.description, Toast.LENGTH_SHORT).show()
            }
        })

        placesViewModel.allPlacesLive.observe(viewLifecycleOwner, Observer { places ->
            places?.let { placesAdapter.setPlaces(it) }
        })

        fab = view.findViewById(R.id.fab);
        fab.setOnClickListener {

            if(placesViewModel.getAllPlacesSize() > 10)
            {
                placesViewModel.clearPlacesTable()
            }
            else
            {
                placesViewModel.insert(PlaceObject("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.", "https://picsum.photos/30${(0..9).random()}/200"))
                fragmentPlacesBinding.placesRecycler.smoothScrollToPosition(placesAdapter.itemCount)
            }
        }

        fragmentPlacesBinding.placesRecycler.adapter = placesAdapter
        fragmentPlacesBinding.placesRecycler.layoutManager = LinearLayoutManager(fragmentPlacesBinding.root.context)

        super.onViewCreated(view, savedInstanceState)
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
            R.id.action_search ->{
                true
            }
            R.id.action_map ->{
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}