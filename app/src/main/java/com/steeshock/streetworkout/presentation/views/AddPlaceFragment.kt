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

        binding.placePositionInput.setOnButtonClickListener {
            getPosition()
        }

        binding.placeCategoriesInput.setOnButtonClickListener {
            showCategories()
        }

        binding.placeImagesInput.setOnButtonClickListener {
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
            binding.placeImagesInput.imageButton.gone()
            binding.placeImagesInput.progressBar.visible()
            binding.placeImagesInput.textInput.setText(R.string.hint_images_loading)
        } else {
            binding.placeImagesInput.imageButton.visible()
            binding.placeImagesInput.progressBar.gone()
            binding.placeImagesInput.textInput.setText(getImagesHint(viewState.selectedImagesCount))
        }
        if (viewState.isLocationInProgress) {
            binding.placePositionInput.imageButton.gone()
            binding.placePositionInput.progressBar.visible()
        } else {
            binding.placePositionInput.imageButton.visible()
            binding.placePositionInput.progressBar.gone()
        }
        if (viewState.isSendingInProgress) {
            binding.progressSending.visible()
        } else {
            binding.progressSending.gone()
        }

        binding.placeTitle.isEnabled = !viewState.isSendingInProgress
        binding.placeDescription.isEnabled = !viewState.isSendingInProgress
        binding.placeAddress.isEnabled = !viewState.isSendingInProgress

        binding.placeCategoriesInput.textInput.isEnabled = !viewState.isSendingInProgress
        binding.placeCategoriesInput.textInput.setText(viewState.selectedCategories)
        binding.placeCategoriesInput.imageButton.isClickable = !viewState.isSendingInProgress

        binding.placePositionInput.textInput.isEnabled = !viewState.isSendingInProgress
        binding.placePositionInput.imageButton.isClickable = !viewState.isSendingInProgress

        binding.placeImagesInput.textInput.isEnabled = !viewState.isSendingInProgress
        binding.placeImagesInput.imageButton.isClickable = !viewState.isSendingInProgress

        binding.clearButton.isClickable = !viewState.isSendingInProgress
        binding.sendButton.isClickable = !viewState.isSendingInProgress
        binding.progressSending.progress = viewState.sendingProgress
        binding.progressSending.max = viewState.maxProgressValue

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
                showPublishPlaceAlertDialog()
            }
            is LocationResult -> {
                handleLocationEvent(viewEvent)
            }
        }
    }

    private fun showPublishPlaceAlertDialog() {
        requireActivity().showAlertDialog(
            title = getString(R.string.attention_title),
            message = getString(R.string.publish_permission_dialog_message),
            positiveText = getString(R.string.ok_item),
            negativeText = getString(R.string.cancel_item),
            onPositiveAction = { viewModel.onAddNewPlace(
                title = binding.placeTitle.text.toString(),
                description = binding.placeDescription.text.toString(),
                position = binding.placePositionInput.textInput.text.toString(),
                address = binding.placeAddress.text.toString(),
            )},
        )
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
            it.placePositionInput.clearInput()
            it.placeImagesInput.clearInput()
            it.placeCategoriesInput.clearInput()
            it.placePositionInput.progressBar.gone()
            it.placePositionInput.imageButton.visible()
            it.placePositionInput.imageButton.isEnabled = true
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
        binding.placePositionInput.textInput.text?.clear()
        viewState.placeLocation?.let {
            binding.placePositionInput.textInput.text?.append("${it.latitude} ${it.longitude}")
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