package com.steeshock.android.streetworkout.viewmodels

import android.net.Uri
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.steeshock.android.streetworkout.data.model.Category
import com.steeshock.android.streetworkout.data.model.Place
import com.steeshock.android.streetworkout.data.repository.interfaces.ICategoriesRepository
import com.steeshock.android.streetworkout.data.repository.interfaces.IPlacesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AddPlaceViewModel(
    private val placesRepository: IPlacesRepository,
    private val categoriesRepository: ICategoriesRepository,
) : ViewModel() {

    val allCategoriesLive: LiveData<List<Category>> = categoriesRepository.allCategories
    val loadCompleted: MutableLiveData<Boolean> = MutableLiveData(false)
    var sendingProgress: MutableLiveData<Int> = MutableLiveData(0)

    var checkedCategoriesArray: BooleanArray? = null
    var selectedCategories: ArrayList<Int> = arrayListOf()

    var selectedImages: MutableList<Uri> = mutableListOf()
    var downloadedImagesLinks: ArrayList<String> = arrayListOf()
    var selectedImagesMessage = ""

    var isImagePickingInProgress: ObservableBoolean = ObservableBoolean(false)
    var isLocationInProgress: ObservableBoolean = ObservableBoolean(false)
    var isSendingInProgress: ObservableBoolean = ObservableBoolean(false)

    var sendingPlace: Place? = null

    fun uploadDataToFirebase(uri: Uri, placeUUID: String) = viewModelScope.launch() {

        val result = placesRepository.uploadImage(uri, placeUUID)
        downloadedImagesLinks.add(result.toString())

        sendingProgress.postValue(sendingProgress.value?.plus(1))
        delay(500)

        if (downloadedImagesLinks.size == selectedImages.size) {
            createAndPublishNewPlace()
        }
    }

    fun createAndPublishNewPlace() = viewModelScope.launch(Dispatchers.IO) {

        sendingPlace?.images = downloadedImagesLinks

        sendingPlace?.let { placesRepository.insertPlaceLocal(it) }
        sendingPlace?.let { placesRepository.insertPlaceRemote(it) }

        sendingProgress.postValue(sendingProgress.value?.plus(1))
        delay(500)

        loadCompleted.postValue(true)
        isSendingInProgress.set(false)
    }
}