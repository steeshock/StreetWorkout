package com.steeshock.android.streetworkout.di.modules

import android.content.Context
import com.steeshock.android.streetworkout.data.api.PlacesAPI
import com.steeshock.android.streetworkout.data.database.CategoriesDao
import com.steeshock.android.streetworkout.data.database.PlacesDao
import com.steeshock.android.streetworkout.data.database.PlacesDatabase
import com.steeshock.android.streetworkout.data.repository.implementation.firebase.FirebaseCategoriesRepository
import com.steeshock.android.streetworkout.data.repository.implementation.firebase.FirebasePlacesRepository
import com.steeshock.android.streetworkout.data.repository.implementation.rxjava.RxJavaCategoriesRepository
import com.steeshock.android.streetworkout.data.repository.implementation.rxjava.RxJavaPlacesRepository
import com.steeshock.android.streetworkout.data.repository.interfaces.ICategoriesRepository
import com.steeshock.android.streetworkout.data.repository.interfaces.IPlacesRepository
import dagger.Module
import dagger.Provides

@Module
class DatabaseModule(private val appContext: Context) {

    @Provides
    fun providePlacesRepository(
        placesDao: PlacesDao,
        placesAPI: PlacesAPI
    ): IPlacesRepository {

        // Реализация для работы с Firebase Realtime Database
        return FirebasePlacesRepository.getInstance(placesDao)

        // Реализация для работы с API на сервере
        //return RxJavaPlacesRepository.getInstance(placesDao, placesAPI)
    }

    @Provides
    fun provideCategoriesRepository(
        categoriesDao: CategoriesDao,
        placesAPI: PlacesAPI
    ): ICategoriesRepository {

        // Реализация для работы с Firebase Realtime Database
        return FirebaseCategoriesRepository.getInstance(categoriesDao)

        // Реализация для работы с API на сервере
        //return RxJavaCategoriesRepository.getInstance(categoriesDao, placesAPI)
    }

    @Provides
    fun providePlacesDao(): PlacesDao {
        return PlacesDatabase
            .getInstance(appContext)
            .getPlacesDao()
    }

    @Provides
    fun provideCategoriesDao(): CategoriesDao {
        return PlacesDatabase
            .getInstance(appContext)
            .getCategoriesDao()
    }
}