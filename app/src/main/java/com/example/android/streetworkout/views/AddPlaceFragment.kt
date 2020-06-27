package com.example.android.streetworkout.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.example.android.streetworkout.data.model.PlaceObject
import com.example.android.streetworkout.databinding.FragmentAddPlaceBinding
import com.example.android.streetworkout.utils.InjectorUtils
import com.example.android.streetworkout.viewmodels.AddPlaceViewModel


class AddPlaceFragment : DialogFragment() {

    private val addPlaceViewModel: AddPlaceViewModel by viewModels {
        InjectorUtils.provideAddPlaceViewModelFactory(requireActivity())
    }

    private lateinit var fragmentAddPlaceBinding: FragmentAddPlaceBinding

    companion object {
        fun newInstance() = AddPlaceFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentAddPlaceBinding = FragmentAddPlaceBinding.inflate(inflater, container, false)
        fragmentAddPlaceBinding.viewmodel = addPlaceViewModel
        fragmentAddPlaceBinding.lifecycleOwner = this
        return fragmentAddPlaceBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragmentAddPlaceBinding.setBackClickListener { dialog?.onBackPressed() }

        fragmentAddPlaceBinding.setDoneClickListener { addNewPlace()}
    }

    private fun addNewPlace() {
        addPlaceViewModel.insert(
            PlaceObject(
                "https://picsum.photos/30${(0..9).random()}/200",
                title = fragmentAddPlaceBinding.placeTitle.text.toString(),
                description = fragmentAddPlaceBinding.placeDescription.text.toString(),
                position = fragmentAddPlaceBinding.placePosition.text.toString(),
                address = fragmentAddPlaceBinding.placeAddress.text.toString()
            )
        )
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            dialog.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            //dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }
}