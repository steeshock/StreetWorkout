package com.example.android.streetworkout.utils

import android.content.Context
import androidx.fragment.app.Fragment
import com.example.android.streetworkout.data.Repository
import com.example.android.streetworkout.data.database.PlacesDatabase

object InjectorUtils {

    private fun getRepository(context: Context): Repository {
        return Repository.getInstance(
            PlacesDatabase.getInstance(context.applicationContext).getPlacesDao()
        )
    }

    fun providePlacesViewModelFactory(
        context: Context
    ): CustomPlacesViewModelFactory {
        return CustomPlacesViewModelFactory(getRepository(context))
    }
}