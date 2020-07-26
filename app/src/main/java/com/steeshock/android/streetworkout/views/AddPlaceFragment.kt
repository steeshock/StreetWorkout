package com.steeshock.android.streetworkout.views

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.ResultReceiver
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.steeshock.android.streetworkout.common.Constants
import com.steeshock.android.streetworkout.services.FetchAddressIntentService
import com.steeshock.android.streetworkout.R
import com.steeshock.android.streetworkout.data.model.Place
import com.steeshock.android.streetworkout.databinding.FragmentAddPlaceBinding
import com.steeshock.android.streetworkout.utils.InjectorUtils
import com.steeshock.android.streetworkout.viewmodels.AddPlaceViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnSuccessListener
import com.steeshock.android.streetworkout.data.model.Category
import kotlinx.coroutines.ExperimentalCoroutinesApi


@ExperimentalCoroutinesApi
class AddPlaceFragment : Fragment() {

    private val TAG = "LocationTag"

    private val REQUEST_PERMISSIONS_REQUEST_CODE = 34

    private var fusedLocationClient: FusedLocationProviderClient? = null

    private var lastLocation: Location? = null

    private var addressRequested = false

    private var addressOutput = ""

    private lateinit var resultReceiver: AddressResultReceiver

    private val addPlaceViewModel: AddPlaceViewModel by viewModels {
        InjectorUtils.provideAddPlaceViewModelFactory(requireActivity())
    }
    private var allCategories = emptyList<Category>()
    private var selectedCategories: MutableSet<Category> = mutableSetOf()

    private lateinit var fragmentAddPlaceBinding: FragmentAddPlaceBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        fragmentAddPlaceBinding = FragmentAddPlaceBinding.inflate(inflater, container, false)
        fragmentAddPlaceBinding.viewmodel = addPlaceViewModel
        fragmentAddPlaceBinding.lifecycleOwner = this

        fragmentAddPlaceBinding.toolbar.setNavigationOnClickListener { view ->
            view.findNavController().navigateUp()
        }

        fragmentAddPlaceBinding.setGetMyPositionClickListener {
            getMyPosition()
        }

        fragmentAddPlaceBinding.setAddCategoryClickListener {
            addRandomCategory()
        }

        fragmentAddPlaceBinding.toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_done -> {
                    addNewPlace()
                    true
                }
                else -> false
            }
        }

        resultReceiver = AddressResultReceiver(Handler())
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        return fragmentAddPlaceBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        addPlaceViewModel.allCategoriesLive.observe(viewLifecycleOwner, Observer { categories ->
            categories?.let { allCategories = it }
        })

        super.onViewCreated(view, savedInstanceState)
    }

    private fun addRandomCategory() {
        val newCategory = Category(allCategories[(allCategories.indices).random()].name, false)

        if (!selectedCategories.contains(newCategory)) {
            val newValue = fragmentAddPlaceBinding.placeCategories.text.append(newCategory.name, "; ")
            selectedCategories.add(newCategory)
            fragmentAddPlaceBinding.placeCategories.text = newValue
        }
    }

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
            addressRequested = true
            updateUIWidgets()

            getAddress()
        }
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
            addressRequested = false
            updateUIWidgets()
        }
    }

    private fun displayAddressOutput() {
        fragmentAddPlaceBinding.placeAddress.setText(addressOutput)
        fragmentAddPlaceBinding.placePosition.setText("${lastLocation?.latitude} ${lastLocation?.longitude}")
    }

    private fun updateUIWidgets() {
        if (addressRequested) {
            fragmentAddPlaceBinding.progressBar.visibility = ProgressBar.VISIBLE
            fragmentAddPlaceBinding.myPositionBtn.isEnabled = false
        } else {
            fragmentAddPlaceBinding.progressBar.visibility = ProgressBar.GONE
            fragmentAddPlaceBinding.myPositionBtn.isEnabled = true
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
                if (addressRequested) startIntentService()
            })?.addOnFailureListener(requireActivity()) { e ->
            Log.w(
                TAG,
                "getLastLocation:onFailure",
                e
            )
        }
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

    private fun addNewPlace() {

        val position = fragmentAddPlaceBinding.placePosition.text.toString().split(" ")

        addPlaceViewModel.insert(
            Place(
                title = fragmentAddPlaceBinding.placeTitle.text.toString(),
                description = fragmentAddPlaceBinding.placeDescription.text.toString(),
                latitude = if (position.size > 1) position[0].toDouble() else 54.513845,
                longitude = if (position.size > 1) position[1].toDouble() else 36.261215,
                address = fragmentAddPlaceBinding.placeAddress.text.toString(),
                categories = selectedCategories.toMutableList()
            )
        )
    }
}