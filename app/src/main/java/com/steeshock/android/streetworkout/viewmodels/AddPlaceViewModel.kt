package com.steeshock.android.streetworkout.viewmodels

import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.steeshock.android.streetworkout.R
import com.steeshock.android.streetworkout.data.model.Category
import com.steeshock.android.streetworkout.data.repository.Repository
import com.steeshock.android.streetworkout.data.model.Place
import com.steeshock.android.streetworkout.data.model.State
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

class AddPlaceViewModel(private val repository: Repository) : ViewModel() {

    val allCategoriesLive: LiveData<List<Category>> = repository.allCategories

    var checkedCategoriesArray: BooleanArray? = null
    var selectedCategories: ArrayList<Int> = arrayListOf()

    fun insertNewPlaceInDatabase(place: Place) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertPlace(place)
    }

    fun insertNewPlaceInFirebase(newPlace: Place) {

        val database = Firebase.database("https://test-projects-b523c-default-rtdb.europe-west1.firebasedatabase.app/")

        val myRef = database.getReference("places").child(newPlace.place_uuid)

        myRef.setValue(newPlace)
    }
}