package com.steeshock.streetworkout.presentation.views

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.Activity
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.ResultReceiver
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import com.steeshock.streetworkout.R
import com.steeshock.streetworkout.common.BaseFragment
import com.steeshock.streetworkout.common.Constants
import com.steeshock.streetworkout.common.appComponent
import com.steeshock.streetworkout.databinding.FragmentAddPlaceBinding
import com.steeshock.streetworkout.extensions.getAlertDialogBuilder
import com.steeshock.streetworkout.extensions.gone
import com.steeshock.streetworkout.extensions.showAlertDialog
import com.steeshock.streetworkout.extensions.visible
import com.steeshock.streetworkout.presentation.viewStates.addPlace.AddPlaceViewEvent
import com.steeshock.streetworkout.presentation.viewStates.addPlace.AddPlaceViewEvent.*
import com.steeshock.streetworkout.presentation.viewStates.addPlace.AddPlaceViewState
import com.steeshock.streetworkout.presentation.viewmodels.AddPlaceViewModel
import com.steeshock.streetworkout.services.geolocation.FetchAddressIntentService
import javax.inject.Inject

class AddPlaceFragment : BaseFragment() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    private val viewModel: AddPlaceViewModel by viewModels { factory }

    private lateinit var imagePicker: ImagePicker.Builder

    private lateinit var resultReceiver: AddressResultReceiver

    private var _binding: FragmentAddPlaceBinding? = null
    private val binding get() = _binding!!

    override fun injectComponent() {
        context?.appComponent?.providePlacesComponent()?.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentAddPlaceBinding.inflate(inflater, container, false)
        resultReceiver = AddressResultReceiver(Handler())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()

        viewModel.viewState.observe(viewLifecycleOwner) {
            renderViewState(it)
        }

        viewModel.viewEvent.observe(viewLifecycleOwner) {
            renderViewEvent(it)
        }
    }

    private fun initViews() {
        binding.toolbar.setNavigationOnClickListener { view ->
            view.findNavController().navigateUp()
        }

        binding.placeTitle.addTextChangedListener {
            if (!it.isNullOrEmpty()) {
                binding.placeTitleInput.error = null
            }
        }

        binding.placeAddress.addTextChangedListener {
            if (!it.isNullOrEmpty()) {
                binding.placeAddressInput.error = null
            }
        }

        binding.myPositionButton.setOnClickListener {
            getPosition()
        }

        binding.categoryButton.setOnClickListener {
            showCategories()
        }

        binding.takeImageButton.setOnClickListener {
            openImagePicker()
        }

        binding.clearButton.setOnClickListener {
            requireActivity().showAlertDialog(
                title = getString(R.string.attention_title),
                message = getString(R.string.clear_fields_dialog_message),
                positiveText = getString(R.string.ok_item),
                negativeText = getString(R.string.cancel_item),
                onPositiveAction = { resetFields() },
            )
        }

        binding.sendButton.setOnClickListener {
            viewModel.onValidateForm(
                placeTitle = binding.placeTitle.text.toString(),
                placeAddress = binding.placeAddress.text.toString(),
            )
        }
        imagePicker = ImagePicker.with(requireActivity())
    }

    private fun renderViewState(viewState: AddPlaceViewState) {
        if (viewState.loadCompleted) {
            resetFields()
        }
        if (viewState.isImagePickingInProgress) {
            binding.takeImageButton.gone()
            binding.progressImageBar.visible()
            binding.placeImages.setText(R.string.hint_images_loading)
        } else {
            binding.takeImageButton.visible()
            binding.progressImageBar.gone()
            binding.placeImages.setText(getImagesHint(viewState.selectedImagesCount))
        }
        if (viewState.isLocationInProgress) {
            binding.myPositionButton.gone()
            binding.progressLocationBar.visible()
        } else {
            binding.myPositionButton.visible()
            binding.progressLocationBar.gone()
        }
        if (viewState.isSendingInProgress) {
            binding.progressSending.visible()
        } else {
            binding.progressSending.gone()
        }

        binding.placeCategories.isEnabled = !viewState.isSendingInProgress
        binding.placeTitle.isEnabled = !viewState.isSendingInProgress
        binding.placeDescription.isEnabled = !viewState.isSendingInProgress
        binding.placePosition.isEnabled = !viewState.isSendingInProgress
        binding.placeAddress.isEnabled = !viewState.isSendingInProgress
        binding.placeImages.isEnabled = !viewState.isSendingInProgress
        binding.categoryButton.isClickable = !viewState.isSendingInProgress
        binding.placeImages.isClickable = !viewState.isSendingInProgress
        binding.takeImageButton.isClickable = !viewState.isSendingInProgress
        binding.clearButton.isClickable = !viewState.isSendingInProgress
        binding.sendButton.isClickable = !viewState.isSendingInProgress
        binding.myPositionButton.isClickable = !viewState.isSendingInProgress
        binding.progressSending.progress = viewState.sendingProgress
        binding.progressSending.max = viewState.maxProgressValue
        binding.placeCategories.setText(viewState.selectedCategories)

        setLocationResult(viewState)
    }

    private fun renderViewEvent(viewEvent: AddPlaceViewEvent) {
        when(viewEvent) {
            ErrorPlaceAddressValidation -> {
                binding.placeAddressInput.error =
                    resources.getString(R.string.required_field_empty_error)
            }
            ErrorPlaceTitleValidation -> {
                binding.placeTitleInput.error =
                    resources.getString(R.string.required_field_empty_error)
            }
            SuccessValidation -> {
                requireActivity().showAlertDialog(
                    title = getString(R.string.attention_title),
                    message = getString(R.string.publish_permission_dialog_message),
                    positiveText = getString(R.string.ok_item),
                    negativeText = getString(R.string.cancel_item),
                    onPositiveAction = { viewModel.onAddNewPlace(
                        title = binding.placeTitle.text.toString(),
                        description = binding.placeDescription.text.toString(),
                        position = binding.placePosition.text.toString(),
                        address = binding.placeAddress.text.toString(),
                    )},
                )
            }
            is LocationResult -> {
                handleLocationEvent(viewEvent)
            }
        }
    }

    private fun getImagesHint(selectedImagesCount: Int): String {
        val imagesHint = if (selectedImagesCount > 0) "$selectedImagesCount" else ""
        return if (imagesHint.isNotEmpty()) {
            getString(R.string.hint_images_attached, imagesHint)
        } else {
            ""
        }
    }

    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                viewModel.onImagePicked(result.data)
            }
            viewModel.onOpenedImagePicker(false)
        }

    private fun resetFields() {
        viewModel.onResetFields()
        binding.let {
            it.placeTitle.text?.clear()
            it.placeDescription.text?.clear()
            it.placeAddress.text?.clear()
            it.placePosition.text?.clear()
            it.placeImages.text?.clear()
            it.placeCategories.text?.clear()
            it.progressLocationBar.gone()
            it.myPositionButton.visible()
            it.myPositionButton.isEnabled = true
            it.placeTitleInput.error = null
            it.placeAddressInput.error = null
        }
        Toast.makeText(requireActivity(), R.string.success_message, Toast.LENGTH_LONG).show()
    }

    private fun openImagePicker() {
        viewModel.onOpenedImagePicker(true)
        imagePicker
            .compress(512)
            .crop(900f, 600f)
            .galleryOnly()
            .setDismissListener {
                viewModel.onOpenedImagePicker(false)
            }
            .createIntent { intent ->
                startForProfileImageResult.launch(intent)
            }
    }

    private fun getPosition() {
        checkPermission(ACCESS_FINE_LOCATION,
        onPermissionGranted = {
            viewModel.onRequestGeolocation()
        })
    }

    private fun showCategories() {
        requireActivity().getAlertDialogBuilder(
            title = getString(R.string.select_category_dialog_title),
            positiveText = getString(R.string.ok_item),
            onPositiveAction = { viewModel.onCategorySelectionConfirmed() },
        )
            .setMultiChoiceItems(
                viewModel.allCategories.value?.map { i -> i.category_name }?.toTypedArray(),
                viewModel.checkedCategoriesArray,
            ) { _, selectedItemId, isChecked ->
                viewModel.onCategoryItemClicked(isChecked, selectedItemId)
            }
            .create()
            .show()
    }

    private fun setLocationResult(viewState: AddPlaceViewState) {
        binding.placeAddress.setText(viewState.placeAddress)
        binding.placePosition.text?.clear()
        viewState.placeLocation?.let {
            binding.placePosition.text?.append("${it.latitude} ${it.longitude}")
        }
    }

    private fun handleLocationEvent(viewEvent: LocationResult) {
        when {
            viewEvent.location != null -> {
                startIntentService(viewEvent.location)
            }
            else -> {
                Toast.makeText(requireActivity(), R.string.try_later_description, Toast.LENGTH_LONG).show()
                viewModel.onAddressReceived()
            }
        }
    }

    private fun startIntentService(lastLocation: Location) {
        val intent = Intent(requireActivity(), FetchAddressIntentService::class.java).apply {
                putExtra(Constants.RECEIVER, resultReceiver)
                putExtra(Constants.LOCATION_DATA_EXTRA, lastLocation)
            }
        requireActivity().startService(intent)
    }

    private inner class AddressResultReceiver(
        handler: Handler,
    ) : ResultReceiver(handler) {
        override fun onReceiveResult(resultCode: Int, resultData: Bundle) {

            // Display the address string or an error message sent from the intent service.
            val addressOutput = resultData.getString(Constants.RESULT_DATA_KEY).toString()
            viewModel.onAddressReceived(addressOutput)

            // Show a toast message if an address was found.
            if (resultCode == Constants.SUCCESS_RESULT) {
                Toast.makeText(requireActivity(), R.string.address_found, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}