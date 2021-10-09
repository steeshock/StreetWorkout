package com.steeshock.android.streetworkout.utils

import android.content.Context
import com.steeshock.android.streetworkout.data.database.PlacesDatabase
import com.steeshock.android.streetworkout.data.repository.implementation.FirebaseCategoriesRepository
import com.steeshock.android.streetworkout.data.repository.implementation.FirebasePlacesRepository
import com.steeshock.android.streetworkout.data.repository.interfaces.ICategoriesRepository
import com.steeshock.android.streetworkout.data.repository.interfaces.IPlacesRepository
import com.steeshock.android.streetworkout.utils.factories.*

object InjectorUtils {

    private fun getPlacesRepository(context: Context): IPlacesRepository {
        return FirebasePlacesRepository.getInstance(
            PlacesDatabase.getInstance(context.applicationContext).getPlacesDao(),
        )
    }

    private fun getCategoriesRepository(context: Context): ICategoriesRepository {
        return FirebaseCategoriesRepository.getInstance(
            PlacesDatabase.getInstance(context.applicationContext).getPlacesDao(),
        )
    }

    fun providePlacesViewModelFactory(
        context: Context
    ): CustomPlacesViewModelFactory {
        return CustomPlacesViewModelFactory(
            getPlacesRepository(context),
            getCategoriesRepository(context),
        )
    }

    fun provideMapViewModelFactory(
        context: Context
    ): CustomMapViewModelFactory {
        return CustomMapViewModelFactory(
            getPlacesRepository(context)
        )
    }

    fun provideFavoritePlacesViewModelFactory(
        context: Context
    ): CustomFavoritePlacesViewModelFactory {
        return CustomFavoritePlacesViewModelFactory(
            getPlacesRepository(context)
        )
    }

    fun provideAddPlaceViewModelFactory(
        context: Context
    ): CustomAddPlaceViewModelFactory {
        return CustomAddPlaceViewModelFactory(
            getPlacesRepository(context),
            getCategoriesRepository(context),
        )
    }

    fun provideProfileViewModelFactory(
        context: Context
    ): CustomProfileViewModelFactory {
        return CustomProfileViewModelFactory(
            getPlacesRepository(context)
        )
    }
}