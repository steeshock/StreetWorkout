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
import com.steeshock.streetworkout.data.repository.implementation.mockApi.SimpleApiCategoriesRepository
import com.steeshock.streetworkout.data.repository.implementation.mockApi.SimpleApiPlacesRepository
import com.steeshock.streetworkout.data.repository.interfaces.ICategoriesRepository
import com.steeshock.streetworkout.data.repository.interfaces.IDataStoreRepository
import com.steeshock.streetworkout.data.repository.interfaces.IPlacesRepository
import com.steeshock.streetworkout.data.repository.interfaces.IUserRepository
import com.steeshock.streetworkout.data.workers.common.IWorkerService
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(includes = [DatabaseModule::class])
interface RepositoryModule {
    @Binds
    @Singleton
    fun bindPlacesRepository(placesRepository: FirebasePlacesRepository): IPlacesRepository
    //fun bindPlacesRepository(placesRepository: SimpleApiPlacesRepository): IPlacesRepository

    @Binds
    @Singleton
    fun bindCategoriesRepository(categoriesRepository: FirebaseCategoriesRepository): ICategoriesRepository
    //fun bindPlacesRepository(categoriesRepository: SimpleApiCategoriesRepository): ICategoriesRepository

    @Binds
    @Singleton
    fun bindUserRepository(userRepository: FirebaseUserRepository): IUserRepository

    @Binds
    @Singleton
    fun bindDataStoreRepository(dataStoreRepository: DataStoreRepository): IDataStoreRepository

}