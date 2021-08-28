package com.steeshock.android.streetworkout.utils

import android.content.Context
import com.steeshock.android.streetworkout.data.database.PlacesDatabase
import com.steeshock.android.streetworkout.data.repository.Repository
import com.steeshock.android.streetworkout.utils.factories.*

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

    fun provideProfileViewModelFactory(
        context: Context
    ): CustomProfileViewModelFactory {
        return CustomProfileViewModelFactory(
            getRepository(context)
        )
    }
}