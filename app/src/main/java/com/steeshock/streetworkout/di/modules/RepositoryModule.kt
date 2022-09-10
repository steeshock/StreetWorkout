package com.steeshock.streetworkout.di.modules

import com.steeshock.streetworkout.data.dataSources.implementation.categories.CategoriesLocalDataSource
import com.steeshock.streetworkout.data.dataSources.implementation.categories.FirebaseCategoriesRemoteDataSource
import com.steeshock.streetworkout.data.dataSources.implementation.places.FirebasePlacesRemoteDataSource
import com.steeshock.streetworkout.data.dataSources.implementation.places.PlacesLocalDataSource
import com.steeshock.streetworkout.data.dataSources.local.ICategoriesLocalDataSource
import com.steeshock.streetworkout.data.dataSources.local.IPlacesLocalDataSource
import com.steeshock.streetworkout.data.dataSources.remote.ICategoriesRemoteDataSource
import com.steeshock.streetworkout.data.dataSources.remote.IPlacesRemoteDataSource
import com.steeshock.streetworkout.data.repository.implementation.CategoriesRepository
import com.steeshock.streetworkout.data.repository.implementation.DataStoreRepository
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
    fun bindCategoriesRepository(categoriesRepository: CategoriesRepository): ICategoriesRepository

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

    @Binds
    @Singleton
    fun bindCategoriesRemoteDataStore(categoriesRemoteDataStore: FirebaseCategoriesRemoteDataSource): ICategoriesRemoteDataSource

    @Binds
    @Singleton
    fun bindCategoriesLocalDataStore(categoriesLocalDataStore: CategoriesLocalDataSource): ICategoriesLocalDataSource
}