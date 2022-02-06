package com.steeshock.android.streetworkout.presentation.viewmodels

import android.content.Intent
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.steeshock.android.streetworkout.data.model.Category
import com.steeshock.android.streetworkout.data.model.Place
import com.steeshock.android.streetworkout.data.repository.interfaces.ICategoriesRepository
import com.steeshock.android.streetworkout.data.repository.interfaces.IPlacesRepository
import com.steeshock.android.streetworkout.presentation.viewStates.AddPlaceViewState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class AddPlaceViewModel @Inject constructor(
    private val placesRepository: IPlacesRepository,
    private val categoriesRepository: ICategoriesRepository,
) : ViewModel() {

    private val mutableViewState: MutableLiveData<AddPlaceViewState> = MutableLiveData()
    val viewState: LiveData<AddPlaceViewState>
        get() = mutableViewState

    val allCategories: LiveData<List<Category>> = categoriesRepository.allCategories

    var checkedCategoriesArray: BooleanArray? = null
    var selectedCategories: ArrayList<Int> = arrayListOf()

    private var selectedImages: MutableList<Uri> = mutableListOf()
    private var downloadedImagesLinks: ArrayList<String> = arrayListOf()

    var sendingPlace: Place? = null

    fun onAddNewPlace(
        place: Place,
        onSuccessValidation: () -> Unit = {},
    ) {
        sendingPlace = place
        mutableViewState.setNewState {
            copy(
                isSendingInProgress = true,
                sendingProgress = 0,
                maxProgressValue = selectedImages.size + 1
            )
        }

        if (selectedImages.size > 0) {
            selectedImages.forEach { uri ->
                uploadDataToFirebase(uri, place.place_uuid)
            }
        } else {
            createAndPublishNewPlace()
        }
    }

    private fun uploadDataToFirebase(uri: Uri, placeUUID: String) = viewModelScope.launch() {

        val result = placesRepository.uploadImage(uri, placeUUID)
        downloadedImagesLinks.add(result.toString())

        mutableViewState.setNewState(postValue = true) { copy(sendingProgress = ++sendingProgress) }
        delay(500)

        if (downloadedImagesLinks.size == selectedImages.size) {
            createAndPublishNewPlace()
        }
    }

    private fun createAndPublishNewPlace() = viewModelScope.launch(Dispatchers.IO) {

        sendingPlace?.images = downloadedImagesLinks

        sendingPlace?.let {
            placesRepository.insertPlaceLocal(it)
            placesRepository.insertPlaceRemote(it)
        }

        mutableViewState.setNewState(postValue = true) {
            copy(
                sendingProgress = ++sendingProgress
            )
        }

        delay(300)

        mutableViewState.setNewState(postValue = true) {
            copy(
                loadCompleted = true,
                isSendingInProgress = false,
                selectedImagesMessage ="",
            )
        }
    }

    fun onResetFields() {
        selectedImages.clear()
        selectedCategories.clear()
        downloadedImagesLinks.clear()
        checkedCategoriesArray = BooleanArray(allCategories.value?.size!!)
        mutableViewState.setNewState(postValue = true) {
            copy(
                loadCompleted = false,
                selectedImagesMessage = "",
            )
        }
    }

    fun onImagePicked(data: Intent?) {
        selectedImages.add(data?.data!!)
        mutableViewState.setNewState {
            copy(
                isImagePickingInProgress = false,
                selectedImagesMessage = "Прикреплено фотографий: ${selectedImages.size}",
            )
        }
    }

    fun onOpenedImagePicker(isPickerVisible: Boolean) {
        mutableViewState.setNewState {
            copy(
                isImagePickingInProgress = isPickerVisible,
            )
        }
    }

    fun onLocationProgressChanged(isLocationInProgress: Boolean) {
        mutableViewState.setNewState {
            copy(
                isLocationInProgress = isLocationInProgress,
            )
        }
    }

    private fun MutableLiveData<AddPlaceViewState>.setNewState(
        postValue: Boolean = false,
        block: AddPlaceViewState.() -> AddPlaceViewState,
    ) {
        val currentState = value ?: AddPlaceViewState()
        val newState = currentState.run { block() }

        if (postValue) {
            postValue(newState)
        } else {
            value = newState
        }
    }
}