package com.steeshock.streetworkout.presentation.viewmodels

import android.content.Intent
import android.location.Location
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.steeshock.streetworkout.data.model.Category
import com.steeshock.streetworkout.data.model.Place
import com.steeshock.streetworkout.data.repository.interfaces.ICategoriesRepository
import com.steeshock.streetworkout.data.repository.interfaces.IPlacesRepository
import com.steeshock.streetworkout.presentation.delegates.ViewEventDelegate
import com.steeshock.streetworkout.presentation.delegates.ViewEventDelegateImpl
import com.steeshock.streetworkout.presentation.delegates.ViewStateDelegate
import com.steeshock.streetworkout.presentation.delegates.ViewStateDelegateImpl
import com.steeshock.streetworkout.presentation.viewStates.addPlace.AddPlaceViewEvent
import com.steeshock.streetworkout.presentation.viewStates.addPlace.AddPlaceViewEvent.*
import com.steeshock.streetworkout.presentation.viewStates.addPlace.AddPlaceViewState
import com.steeshock.streetworkout.domain.repository.IAuthService
import com.steeshock.streetworkout.services.geolocation.GeolocationService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

class AddPlaceViewModel @Inject constructor(
    private val placesRepository: IPlacesRepository,
    categoriesRepository: ICategoriesRepository,
    private val authService: IAuthService,
    private val geolocationService: GeolocationService,
) : ViewModel(),
    ViewEventDelegate<AddPlaceViewEvent> by ViewEventDelegateImpl(),
    ViewStateDelegate<AddPlaceViewState> by ViewStateDelegateImpl({AddPlaceViewState()}) {

    val allCategories: LiveData<List<Category>> = categoriesRepository.allCategories

    private val selectedCategories: ArrayList<Int> = arrayListOf()
    private val tempSelectedCategories: ArrayList<Int> = arrayListOf()

    val checkedCategoriesArray: BooleanArray?
        get() = getCheckedCategories()

    private var selectedImages: MutableList<Uri> = mutableListOf()
    private var downloadedImagesLinks: MutableList<String> = mutableListOf()

    private var sendingPlace: Place? = null

    var lastLocation: Location? = null

    fun onAddNewPlace(
        title: String,
        description: String,
        position: String,
        address: String,
    ) {
        sendingPlace = getNewPlace(title, description, position, address)

        updateViewState {
            copy(
                isSendingInProgress = true,
                sendingProgress = 0,
                maxProgressValue = selectedImages.size + 1
            )
        }

        if (selectedImages.size > 0) {
            uploadDataToFirebase(selectedImages, sendingPlace?.placeId)
        } else {
            createAndPublishNewPlace()
        }
    }

    fun onResetFields() {
        selectedImages.clear()
        selectedCategories.clear()
        downloadedImagesLinks.clear()
        updateViewState(postValue = true) {
            copy(
                loadCompleted = false,
                selectedImagesCount = 0,
                selectedCategories = "",
                placeAddress = "",
                placeLocation = null,
            )
        }
    }

    fun onImagePicked(data: Intent?) {
        selectedImages.add(data?.data!!)
        updateViewState {
            copy(
                isImagePickingInProgress = false,
                selectedImagesCount = selectedImages.size,
            )
        }
    }

    fun onOpenedImagePicker(isPickerVisible: Boolean) {
        updateViewState {
            copy(
                isImagePickingInProgress = isPickerVisible,
            )
        }
    }

    fun onCategoryItemClicked(isChecked: Boolean, selectedItemId: Int) {
        val selectedCategoryId = allCategories.value?.get(selectedItemId)?.category_id
        when {
            isChecked -> selectedCategoryId?.let { tempSelectedCategories.add(it) }
            else -> selectedCategoryId?.let { tempSelectedCategories.remove(it) }
        }
    }

    fun onCategorySelectionConfirmed() {
        selectedCategories.clear()
        selectedCategories.addAll(tempSelectedCategories)
        tempSelectedCategories.clear()

        val selectedCategoriesString = allCategories.value
            ?.filter { category -> selectedCategories.contains(category.category_id) }
            ?.map { it.category_name }
            ?.toString()
            ?.removeSurrounding("[", "]")
            ?: ""

        updateViewState {
            copy(
                selectedCategories = selectedCategoriesString,
            )
        }
    }

    fun onRequestGeolocation() = viewModelScope.launch(Dispatchers.IO) {
        updateViewState(postValue = true) { copy(isLocationInProgress = true) }
        try {
            if (lastLocation == null) {
                lastLocation = geolocationService.getLastLocation()
            }
        } catch (e: Exception) {
            lastLocation = null
            updateViewState(postValue = true) { copy(isLocationInProgress = false) }
        } finally {
            postViewEvent(LocationResult(lastLocation))
        }
    }

    fun onAddressReceived(addressOutput: String? = null) {
        updateViewState(postValue = true) {
            copy(
                placeAddress = addressOutput,
                placeLocation = lastLocation,
                isLocationInProgress = false,
            )
        }
    }

    fun onValidateForm(
        placeTitle: String,
        placeAddress: String,
    ) {
        val isSuccessValidation = validateTitle(placeTitle) and
                validateAddress(placeAddress)

        if (isSuccessValidation) {
            sendViewEvent(SuccessValidation)
        }
    }

    private fun validateTitle(placeTitle: String): Boolean {
        return if (placeTitle.isEmpty()) {
            sendViewEvent(ErrorPlaceTitleValidation)
            false
        } else {
            true
        }
    }

    private fun validateAddress(placeAddress: String): Boolean {
        return if (placeAddress.isEmpty()) {
            sendViewEvent(ErrorPlaceAddressValidation)
            false
        } else {
            true
        }
    }

    private fun getNewPlace(
        title: String,
        description: String,
        position: String,
        address: String,
    ): Place {
        val placeId = UUID.randomUUID().toString()
        val userId = authService.currentUserId
        val positionValues = position.split(" ")

        return Place(
            placeId = placeId,
            userId = userId,
            title = title,
            description = description,
            latitude = if (positionValues.size > 1) position[0].code.toDouble() else 54.513845,
            longitude = if (positionValues.size > 1) position[1].code.toDouble() else 36.261215,
            address = address,
            categories = selectedCategories
        )
    }

    private fun uploadDataToFirebase(selectedImages: MutableList<Uri>, placeId: String?) = viewModelScope.launch {
        selectedImages.forEach { uri ->
            val result = placesRepository.uploadImage(uri, placeId)
            downloadedImagesLinks.add(result.toString())

            updateViewState(postValue = true) { copy(sendingProgress = ++sendingProgress) }

            if (downloadedImagesLinks.size == selectedImages.size) {
                createAndPublishNewPlace()
            }
        }
    }

    private fun createAndPublishNewPlace() = viewModelScope.launch(Dispatchers.IO) {

        sendingPlace?.images = downloadedImagesLinks

        sendingPlace?.let {
            placesRepository.insertPlaceLocal(it)
            placesRepository.insertPlaceRemote(it)
        }

        updateViewState(postValue = true) {
            copy(
                sendingProgress = ++sendingProgress
            )
        }

        delay(300)

        updateViewState(postValue = true) {
            copy(
                loadCompleted = true,
                isSendingInProgress = false,
                selectedImagesCount = 0,
            )
        }
    }

    private fun getCheckedCategories(): BooleanArray? {
        tempSelectedCategories.clear()
        tempSelectedCategories.addAll(selectedCategories)
        return allCategories.value?.map {
            selectedCategories.contains(it.category_id)
        }?.toBooleanArray()
    }
}