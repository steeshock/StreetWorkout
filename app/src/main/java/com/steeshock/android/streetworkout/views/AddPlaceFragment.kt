package com.steeshock.android.streetworkout.views

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.ResultReceiver
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnSuccessListener
import com.steeshock.android.streetworkout.R
import com.steeshock.android.streetworkout.common.BaseFragment
import com.steeshock.android.streetworkout.common.Constants
import com.steeshock.android.streetworkout.common.appComponent
import com.steeshock.android.streetworkout.data.factories.AddPlaceViewModelFactory
import com.steeshock.android.streetworkout.data.model.Category
import com.steeshock.android.streetworkout.data.model.Place
import com.steeshock.android.streetworkout.databinding.FragmentAddPlaceBinding
import com.steeshock.android.streetworkout.services.FetchAddressIntentService
import com.steeshock.android.streetworkout.viewmodels.AddPlaceViewModel
import java.util.*
import javax.inject.Inject

class AddPlaceFragment : BaseFragment(), IAddPlace {

    @Inject
    lateinit var factory: AddPlaceViewModelFactory

    private val addPlaceViewModel: AddPlaceViewModel by viewModels { factory }

    private lateinit var imagePicker: ImagePicker.Builder

    private var _fragmentAddPlaceBinding: FragmentAddPlaceBinding? = null
    private val fragmentAddPlaceBinding get() = _fragmentAddPlaceBinding!!

    private var allCategories = emptyList<Category>()

    override fun injectComponent() {
        context?.appComponent?.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _fragmentAddPlaceBinding = FragmentAddPlaceBinding.inflate(inflater, container, false)
        fragmentAddPlaceBinding.viewmodel = addPlaceViewModel
        fragmentAddPlaceBinding.lifecycleOwner = this

        fragmentAddPlaceBinding.fragmentBehaviour = this

        fragmentAddPlaceBinding.toolbar.setNavigationOnClickListener { view ->
            view.findNavController().navigateUp()
        }

        fragmentAddPlaceBinding.placeTitle.addTextChangedListener {
            if (!it.isNullOrEmpty()) {
                fragmentAddPlaceBinding.placeTitleInput.error = null
            }
        }

        fragmentAddPlaceBinding.placeAddress.addTextChangedListener {
            if (!it.isNullOrEmpty()) {
                fragmentAddPlaceBinding.placeAddressInput.error = null
            }
        }

        resultReceiver = AddressResultReceiver(Handler())
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        imagePicker = ImagePicker.with(requireActivity())

        return fragmentAddPlaceBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addPlaceViewModel.allCategoriesLive.observe(viewLifecycleOwner, Observer { categories ->
            categories?.let { allCategories = it }

            if (addPlaceViewModel.checkedCategoriesArray == null) {
                addPlaceViewModel.checkedCategoriesArray = BooleanArray(allCategories.size)
            }
        })

        addPlaceViewModel.loadCompleted.observe(viewLifecycleOwner, Observer {
            if (it) {
                resetFields()
            }
        })
    }

    //region Categories
    private fun getClearFieldsDialog(): Dialog {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireActivity())
        return builder
            .setTitle(getString(R.string.clear_fields_alert))
            .setMessage(getString(R.string.clear_fields_message))
            .setPositiveButton(getString(R.string.ok_item)) { _, _ -> resetFields() }
            .setNegativeButton(getString(R.string.cancel_item), null)
            .create()
    }

    private fun getCategoriesDialog(): Dialog {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireActivity())
        return builder
            .setTitle(getString(R.string.select_category_alert))
            .setMultiChoiceItems(
                allCategories.map { i -> i.category_name }.toTypedArray(),
                addPlaceViewModel.checkedCategoriesArray
            ) { _, which, isChecked ->

                val selectedCategory = allCategories[which]

                if (isChecked) {
                    selectedCategory.category_id?.let { addPlaceViewModel.selectedCategories.add(it) }
                } else {
                    selectedCategory.category_id?.let {
                        addPlaceViewModel.selectedCategories.remove(
                            it
                        )
                    }
                }
            }
            .setPositiveButton(getString(R.string.ok_item)) { _, _ -> addCategories() }
            .create()
    }

    private fun addCategories() {
        if (addPlaceViewModel.selectedCategories.isEmpty()) {
            fragmentAddPlaceBinding.placeCategories.text.clear()
            return
        }

        fragmentAddPlaceBinding.placeCategories.text.clear()

        addPlaceViewModel.selectedCategories.forEach { i ->
            run {
                val category = allCategories.find { j -> j.category_id == i }?.category_name

                if (!category.isNullOrEmpty()) {
                    fragmentAddPlaceBinding.placeCategories.text.append(
                        category,
                        "; "
                    )
                }
            }
        }
    }
    //endregion

    //region Image picking
    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->

            val resultCode = result.resultCode
            val data = result.data

            if (resultCode == Activity.RESULT_OK) {

                addPlaceViewModel.selectedImages.add(data?.data!!)

                addPlaceViewModel.selectedImagesMessage =
                    "Прикреплено фотографий: ${addPlaceViewModel.selectedImages.size}"
            }

            addPlaceViewModel.isImagePickingInProgress.set(false)
        }
    //endregion

    private fun addNewPlace() {

        val placeUUID = UUID.randomUUID().toString()
        addPlaceViewModel.sendingPlace = createNewPlace(placeUUID)

        addPlaceViewModel.isSendingInProgress.set(true)
        addPlaceViewModel.sendingProgress.postValue(0)

        fragmentAddPlaceBinding.progressSending.max = addPlaceViewModel.selectedImages.size + 1

        if (addPlaceViewModel.selectedImages.size > 0) {
            addPlaceViewModel.selectedImages.forEach { uri ->
                addPlaceViewModel.uploadDataToFirebase(uri, placeUUID)
            }
        } else {
            addPlaceViewModel.createAndPublishNewPlace()
        }
    }

    private fun validatePlace(): Boolean {

        var validationResult = true;

        if (fragmentAddPlaceBinding.placeTitle.text.isNullOrEmpty()) {
            fragmentAddPlaceBinding.placeTitleInput.error =
                resources.getString(R.string.required_field_empty_error)
            validationResult = false;
        }

        if (fragmentAddPlaceBinding.placeAddress.text.isNullOrEmpty()) {
            fragmentAddPlaceBinding.placeAddressInput.error =
                resources.getString(R.string.required_field_empty_error)
            validationResult = false;
        }

        return validationResult;
    }

    private fun createNewPlace(placeUUID: String): Place {

        val position = fragmentAddPlaceBinding.placePosition.text.toString().split(" ")

        return Place(
            place_uuid = placeUUID,
            title = fragmentAddPlaceBinding.placeTitle.text.toString(),
            description = fragmentAddPlaceBinding.placeDescription.text.toString(),
            latitude = if (position.size > 1) position[0].toDouble() else 54.513845,
            longitude = if (position.size > 1) position[1].toDouble() else 36.261215,
            address = fragmentAddPlaceBinding.placeAddress.text.toString(),
            categories = addPlaceViewModel.selectedCategories
        )
    }

    private fun resetFields() {

        fragmentAddPlaceBinding.let {
            it.placeTitle.text?.clear()
            it.placeDescription.text?.clear()
            it.placeAddress.text?.clear()
            it.placePosition.text.clear()
            it.placeImages.text.clear()
            it.placeCategories.text.clear()
            it.progressLocationBar.visibility = View.GONE
            it.myPositionBtn.visibility = View.VISIBLE
            it.myPositionBtn.isEnabled = true

            addPlaceViewModel.selectedCategories.clear()
            addPlaceViewModel.checkedCategoriesArray = BooleanArray(allCategories.size)

            addPlaceViewModel.selectedImagesMessage = ""
            addPlaceViewModel.selectedImages.clear()
            addPlaceViewModel.downloadedImagesLinks.clear()
            addPlaceViewModel.isImagePickingInProgress.set(false)

            fragmentAddPlaceBinding.placeTitleInput.error = null
            fragmentAddPlaceBinding.placeAddressInput.error = null
        }

        Toast.makeText(requireActivity(), R.string.success_message, Toast.LENGTH_LONG).show()
    }

    private fun getPublishPermissionDialog(): Dialog {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireActivity())
        return builder
            .setTitle(getString(R.string.clear_fields_alert))
            .setMessage(getString(R.string.publish_permission_message))
            .setPositiveButton(getString(R.string.ok_item)) { _, _ -> addNewPlace() }
            .setNegativeButton(getString(R.string.cancel_item), null)
            .create()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _fragmentAddPlaceBinding = null
    }

    // region Fragment behaviour

    override fun getPosition(view: View) {
        getMyPosition()
    }

    override fun openImagePicker(view: View) {
        addPlaceViewModel.isImagePickingInProgress.set(true)

        imagePicker
            .compress(512)
            .crop(900f, 600f)
            .galleryOnly()
            .setDismissListener {
                addPlaceViewModel.isImagePickingInProgress.set(false)
            }
            .createIntent { intent ->
                startForProfileImageResult.launch(intent)
            }
    }

    override fun addNewPlace(view: View) {
        if (validatePlace()) {
            getPublishPermissionDialog().show()
        }
    }

    override fun resetFields(view: View) {
        getClearFieldsDialog().show()
    }

    override fun showCategories(view: View) {
        getCategoriesDialog().show()
    }

    override fun handleTitleChanged(editable: Editable?) {
        if (!editable.isNullOrEmpty()) {
            fragmentAddPlaceBinding.placeTitleInput.error = null
        }
    }

    override fun handleAddressChanged(editable: Editable?) {
        if (!editable.isNullOrEmpty()) {
            fragmentAddPlaceBinding.placeAddressInput.error = null
        }
    }
    // endregion

    //region GPS
    private val TAG = "LocationTag"

    private val REQUEST_PERMISSIONS_REQUEST_CODE = 34

    private var fusedLocationClient: FusedLocationProviderClient? = null

    private var lastLocation: Location? = null

    private var addressOutput = ""

    private lateinit var resultReceiver: AddressResultReceiver

    private fun getMyPosition() {
        if (!checkPermissions()) {
            requestPermissions()
        } else {
            if (lastLocation != null) {
                startIntentService()
                return
            }

            // If we have not yet retrieved the user location, we process the user's request by setting
            // addressRequested to true. As far as the user is concerned, pressing the Fetch Address
            // button immediately kicks off the process of getting the address.
            addPlaceViewModel.isLocationInProgress.set(true)

            getAddress()
        }
    }


    @SuppressLint("MissingPermission")
    private fun getAddress() {
        fusedLocationClient?.lastLocation?.addOnSuccessListener(
            requireActivity(),
            OnSuccessListener { location ->
                if (location == null) {
                    Log.w(TAG, "onSuccess:null")
                    return@OnSuccessListener
                }

                lastLocation = location

                // Determine whether a Geocoder is available.
                if (!Geocoder.isPresent()) {
                    Toast.makeText(
                        requireActivity(),
                        R.string.no_geocoder_available,
                        Toast.LENGTH_LONG
                    ).show()
                    return@OnSuccessListener
                }

                // If the user pressed the fetch address button before we had the location,
                // this will be set to true indicating that we should kick off the intent
                // service after fetching the location.
                if (addPlaceViewModel.isLocationInProgress.get()) startIntentService()
            })?.addOnFailureListener(requireActivity()) { e ->
            Log.w(
                TAG,
                "getLastLocation:onFailure",
                e
            )
        }
    }

    /**
     * Creates an intent, adds location data to it as an extra, and starts the intent service for
     * fetching an address.
     */
    private fun startIntentService() {
        // Create an intent for passing to the intent service responsible for fetching the address.
        val intent: Intent =
            Intent(requireActivity(), FetchAddressIntentService::class.java).apply {
                // Pass the result receiver as an extra to the service.
                putExtra(Constants.RECEIVER, resultReceiver)

                // Pass the location data as an extra to the service.
                putExtra(Constants.LOCATION_DATA_EXTRA, lastLocation)
            }

        // Start the service. If the service isn't already running, it is instantiated and started
        // (creating a process for it if needed); if it is running then it remains running. The
        // service kills itself automatically once all intents are processed.
        requireActivity().startService(intent)
    }

    /**
     * Receiver for data sent from FetchAddressIntentService.
     */
    private inner class AddressResultReceiver internal constructor(
        handler: Handler
    ) : ResultReceiver(handler) {

        /**
         * Receives data sent from FetchAddressIntentService and updates the UI in MainActivity.
         */
        override fun onReceiveResult(resultCode: Int, resultData: Bundle) {

            // Display the address string or an error message sent from the intent service.
            addressOutput = resultData.getString(Constants.RESULT_DATA_KEY).toString()
            displayAddressOutput()

            // Show a toast message if an address was found.
            if (resultCode == Constants.SUCCESS_RESULT) {
                Toast.makeText(requireActivity(), R.string.address_found, Toast.LENGTH_SHORT).show()
            }

            // Reset. Enable the Fetch Address button and stop showing the progress bar.
            addPlaceViewModel.isLocationInProgress.set(false)
        }
    }

    private fun displayAddressOutput() {
        fragmentAddPlaceBinding.placeAddress.setText(addressOutput)
        fragmentAddPlaceBinding.placePosition.text.clear()
        fragmentAddPlaceBinding.placePosition.text.append("${lastLocation?.latitude} ${lastLocation?.longitude}")
    }

    private fun checkPermissions(): Boolean {
        val permissionState = ActivityCompat.checkSelfPermission(
            requireActivity(),
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        return permissionState == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        val shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(
            requireActivity(),
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.")

            Toast.makeText(requireActivity(), R.string.permission_rationale, Toast.LENGTH_LONG)
                .show()

            startLocationPermissionRequest()

        } else {
            Log.i(TAG, "Requesting permission")
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            startLocationPermissionRequest()
        }
    }

    private fun startLocationPermissionRequest() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            REQUEST_PERMISSIONS_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        Log.i(TAG, "onRequestPermissionResult")

        if (requestCode != REQUEST_PERMISSIONS_REQUEST_CODE) return

        when {
            grantResults.isEmpty() ->
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.")
            grantResults[0] == PackageManager.PERMISSION_GRANTED -> // Permission granted.
                getAddress()
            else -> // Permission denied.
                Toast.makeText(
                    requireActivity(),
                    R.string.permission_denied_explanation,
                    Toast.LENGTH_LONG
                ).show()
        }
    }
    //endregion
}

    interface IAddPlace {
        fun getPosition(view: View)
        fun openImagePicker(view: View)
        fun addNewPlace(view: View)
        fun resetFields(view: View)
        fun showCategories(view: View)
        fun handleTitleChanged(editable: Editable?)
        fun handleAddressChanged(editable: Editable?)
    }