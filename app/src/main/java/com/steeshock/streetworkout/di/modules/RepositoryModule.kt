package com.steeshock.streetworkout.di.modules

import com.steeshock.streetworkout.data.dataSources.implementation.FirebasePlacesRemoteDataSource
import com.steeshock.streetworkout.data.dataSources.implementation.PlacesLocalDataSource
import com.steeshock.streetworkout.data.dataSources.local.IPlacesLocalDataSource
import com.steeshock.streetworkout.data.dataSources.remote.IPlacesRemoteDataSource
import com.steeshock.streetworkout.data.repository.implementation.DataStoreRepository
import com.steeshock.streetworkout.data.repository.implementation.firebase.FirebaseCategoriesRepository
import com.steeshock.streetworkout.data.repository.implementation.PlacesRepository
import com.steeshock.streetworkout.data.repository.implementation.firebase.FirebaseUserRepository
import com.steeshock.streetworkout.domain.repository.ICategoriesRepository
import com.steeshock.streetworkout.domain.repository.IDataStoreRepository
import com.steeshock.streetworkout.domain.repository.IPlacesRepository
import com.steeshock.streetworkout.domain.repository.IUserRepository
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module(includes = [DatabaseModule::class])
interface RepositoryModule {
    @Binds
    @Singleton
    fun bindPlacesRepository(placesRepository: PlacesRepository): IPlacesRepository

    @Binds
    @Singleton
    fun bindCategoriesRepository(categoriesRepository: FirebaseCategoriesRepository): ICategoriesRepository

    @Binds
    @Singleton
    fun bindUserRepository(userRepository: FirebaseUserRepository): IUserRepository

    @Binds
    @Singleton
    fun bindDataStoreRepository(dataStoreRepository: DataStoreRepository): IDataStoreRepository

    @Binds
    @Singleton
    fun bindPlacesRemoteDataStore(placesRemoteDataStore: FirebasePlacesRemoteDataSource): IPlacesRemoteDataSource

    @Binds
    @Singleton
    fun bindPlacesLocalDataStore(placesLocalDataStore: PlacesLocalDataSource): IPlacesLocalDataSource
}