package com.example.android.streetworkout.utils

import android.content.Context
import com.example.android.streetworkout.data.Repository
import com.example.android.streetworkout.data.database.PlacesDatabase
import com.example.android.streetworkout.utils.factories.CustomAddPlaceViewModelFactory
import com.example.android.streetworkout.utils.factories.CustomFavoritePlacesViewModelFactory
import com.example.android.streetworkout.utils.factories.CustomMapViewModelFactory
import com.example.android.streetworkout.utils.factories.CustomPlacesViewModelFactory

object InjectorUtils {

    private fun getRepository(context: Context): Repository {
        return Repository.getInstance(
            PlacesDatabase.getInstance(context.applicationContext).getPlacesDao(),
            ApiUtils.getInstance()
        )
    }

    fun providePlacesViewModelFactory(
        context: Context
    ): CustomPlacesViewModelFactory {
        return CustomPlacesViewModelFactory(
            getRepository(context)
        )
    }

    fun provideMapViewModelFactory(
        context: Context
    ): CustomMapViewModelFactory {
        return CustomMapViewModelFactory(
            getRepository(context)
        )
    }

    fun provideFavoritePlacesViewModelFactory(
        context: Context
    ): CustomFavoritePlacesViewModelFactory {
        return CustomFavoritePlacesViewModelFactory(
            getRepository(context)
        )
    }

    fun provideAddPlaceViewModelFactory(
        context: Context
    ): CustomAddPlaceViewModelFactory {
        return CustomAddPlaceViewModelFactory(
            getRepository(context)
        )
    }
}