package com.example.android.streetworkout

import android.os.Bundle
import androidx.navigation.NavDirections

class FragmentDirections private constructor() {
    private class ActionPlacesFragmentToAddPlaceFragment : NavDirections {

        override fun getActionId(): Int = R.id.action_navigation_places_to_navigation_add_place

        override fun getArguments(): Bundle {
            return Bundle()
        }
    }

    companion object {
        fun actionPlacesFragmentToAddPlaceFragment() : NavDirections =
            ActionPlacesFragmentToAddPlaceFragment()
    }
}