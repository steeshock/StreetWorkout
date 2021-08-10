package com.steeshock.android.streetworkout.viewmodels

import android.net.Uri
import android.widget.Toast
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableInt
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
import kotlinx.coroutines.*
import java.util.*
import kotlin.collections.ArrayList

class AddPlaceViewModel(private val repository: Repository) : ViewModel() {

    val uiDispatcher = Dispatchers.Main.immediate
    val job = SupervisorJob()
    val modelScope = CoroutineScope(uiDispatcher + job)

    val allCategoriesLive: LiveData<List<Category>> = repository.allCategories
    val loadCompleted: MutableLiveData<Boolean> = MutableLiveData(false)

    var checkedCategoriesArray: BooleanArray? = null
    var selectedCategories: ArrayList<Int> = arrayListOf()

    var selectedImages: MutableList<Uri> = mutableListOf()
    var downloadedImagesLinks: ArrayList<String> = arrayListOf()
    var selectedImagesMessage = ""

    var isImagePickingInProgress: ObservableBoolean = ObservableBoolean(false)
    var isLocationInProgress: ObservableBoolean = ObservableBoolean(false)
    var isSendingProgress: ObservableBoolean = ObservableBoolean(false)
    var sendingProgress: ObservableInt = ObservableInt(0)

    var sendingPlace: Place? = null

    fun uploadDataToFirebase(uri: Uri, placeUUID: String) {

        sendingProgress.set(0)

        modelScope.launch{

            val result = repository.uploadImageToFirebase(uri, placeUUID)
            downloadedImagesLinks.add(result.toString())

            sendingProgress.set(sendingProgress.get() + 1)
            delay(500)

            if (downloadedImagesLinks.size == selectedImages.size) {
                createAndPublishNewPlace()
                isSendingProgress.set(false)
            }
        }
    }

    fun createAndPublishNewPlace() = viewModelScope.launch(Dispatchers.IO) {

        sendingPlace?.images = downloadedImagesLinks

        sendingPlace?.let { insertNewPlaceInDatabase(it) }
        sendingPlace?.let { insertNewPlaceInFirebase(it) }

        sendingProgress.set(sendingProgress.get() + 1)
        delay(500)

        loadCompleted.postValue(true)
        isSendingProgress.set(false)
    }

    fun insertNewPlaceInDatabase(place: Place) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertPlace(place)
    }

    fun insertNewPlaceInFirebase(newPlace: Place) {

        val database = Firebase.database("https://test-projects-b523c-default-rtdb.europe-west1.firebasedatabase.app/")

        val myRef = database.getReference("places").child(newPlace.place_uuid)

        myRef.setValue(newPlace)
    }
}