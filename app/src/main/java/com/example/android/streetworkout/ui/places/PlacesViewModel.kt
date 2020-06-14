package com.example.android.streetworkout.ui.places

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.android.streetworkout.data.model.PlaceObject
import com.example.android.streetworkout.data.Repository

class PlacesViewModel(var repository: Repository?) : ViewModel() {

    var places: LiveData<List<PlaceObject>> = repository!!.getPlacesLive()

    val itemsMock = listOf(
        PlaceObject("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.", "https://picsum.photos/301/200"),
        PlaceObject("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.", "https://picsum.photos/302/200"),
        PlaceObject("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.", "https://picsum.photos/303/200"),
        PlaceObject("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.", "https://picsum.photos/304/200"),
        PlaceObject("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.", "https://picsum.photos/305/200"),
        PlaceObject("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.", "https://picsum.photos/306/200"),
        PlaceObject("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.", "https://picsum.photos/307/200")
    )
}