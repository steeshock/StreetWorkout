package com.steeshock.streetworkout.di.modules

import android.content.Context
import com.steeshock.streetworkout.data.api.PlacesAPI
import com.steeshock.streetworkout.data.database.CategoriesDao
import com.steeshock.streetworkout.data.database.PlacesDao
import com.steeshock.streetworkout.data.database.PlacesDatabase
import com.steeshock.streetworkout.data.database.UserDao
import com.steeshock.streetworkout.data.repository.implementation.DataStoreRepository
import com.steeshock.streetworkout.data.repository.implementation.firebase.FirebaseCategoriesRepository
import com.steeshock.streetworkout.data.repository.implementation.firebase.FirebasePlacesRepository
import com.steeshock.streetworkout.data.repository.implementation.firebase.FirebaseUserRepository
import com.steeshock.streetworkout.data.repository.interfaces.ICategoriesRepository
import com.steeshock.streetworkout.data.repository.interfaces.IDataStoreRepository
import com.steeshock.streetworkout.data.repository.interfaces.IPlacesRepository
import com.steeshock.streetworkout.data.repository.interfaces.IUserRepository
import com.steeshock.streetworkout.data.workers.common.IWorkerService
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RepositoryModule {

    @Provides
    @Singleton
    fun providePlacesRepository(
        placesDao: PlacesDao,
        placesAPI: PlacesAPI,
    ): IPlacesRepository {

        // Реализация для работы с Firebase Realtime Database
        return FirebasePlacesRepository(placesDao)

        // Реализация для работы с Mock API на сервере
        //return MockApiPlacesRepository(placesDao, placesAPI)
    }

    @Provides
    @Singleton
    fun provideCategoriesRepository(
        categoriesDao: CategoriesDao,
        placesAPI: PlacesAPI,
    ): ICategoriesRepository {

        // Реализация для работы с Firebase Realtime Database
        return FirebaseCategoriesRepository(categoriesDao)

        // Реализация для работы с Mock API на сервере
        //return MockApiCategoriesRepository(categoriesDao, placesAPI)
    }

    @Provides
    @Singleton
    fun provideUserRepository(
        userDao: UserDao,
        workerService: IWorkerService,
    ): IUserRepository {
        return FirebaseUserRepository(userDao, workerService)
    }

    @Provides
    @Singleton
    fun provideDataStoreRepository(appContext: Context): IDataStoreRepository {
        return DataStoreRepository(appContext)
    }

    @Provides
    fun providePlacesDao(appContext: Context): PlacesDao {
        return PlacesDatabase
            .getInstance(appContext)
            .getPlacesDao()
    }

    @Provides
    fun provideCategoriesDao(appContext: Context): CategoriesDao {
        return PlacesDatabase
            .getInstance(appContext)
            .getCategoriesDao()
    }

    @Provides
    fun provideUserDao(appContext: Context): UserDao {
        return PlacesDatabase
            .getInstance(appContext)
            .getUserDao()
    }
}