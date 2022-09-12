package com.steeshock.streetworkout.di.modules

import com.steeshock.streetworkout.data.dataSources.implementation.categories.CategoriesLocalDataSource
import com.steeshock.streetworkout.data.dataSources.implementation.categories.FirebaseCategoriesRemoteDataSource
import com.steeshock.streetworkout.data.dataSources.implementation.places.FirebasePlacesRemoteDataSource
import com.steeshock.streetworkout.data.dataSources.implementation.places.PlacesLocalDataSource
import com.steeshock.streetworkout.data.dataSources.implementation.user.FirebaseUserRemoteDataSource
import com.steeshock.streetworkout.data.dataSources.implementation.user.UserLocalDataSource
import com.steeshock.streetworkout.data.dataSources.interfaces.local.ICategoriesLocalDataSource
import com.steeshock.streetworkout.data.dataSources.interfaces.local.IPlacesLocalDataSource
import com.steeshock.streetworkout.data.dataSources.interfaces.local.IUserLocalDataSource
import com.steeshock.streetworkout.data.dataSources.interfaces.remote.ICategoriesRemoteDataSource
import com.steeshock.streetworkout.data.dataSources.interfaces.remote.IPlacesRemoteDataSource
import com.steeshock.streetworkout.data.dataSources.interfaces.remote.IUserRemoteDataSource
import com.steeshock.streetworkout.data.repository.implementation.CategoriesRepository
import com.steeshock.streetworkout.data.repository.implementation.DataStoreRepository
import com.steeshock.streetworkout.data.repository.implementation.PlacesRepository
import com.steeshock.streetworkout.data.repository.implementation.UserRepository
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
    fun bindPlacesRepository(impl: PlacesRepository): IPlacesRepository

    @Binds
    @Singleton
    fun bindCategoriesRepository(impl: CategoriesRepository): ICategoriesRepository

    @Binds
    @Singleton
    fun bindUserRepository(impl: UserRepository): IUserRepository

    @Binds
    @Singleton
    fun bindDataStoreRepository(impl: DataStoreRepository): IDataStoreRepository

    @Binds
    @Singleton
    fun bindPlacesRemoteDataStore(impl: FirebasePlacesRemoteDataSource): IPlacesRemoteDataSource

    @Binds
    @Singleton
    fun bindPlacesLocalDataStore(impl: PlacesLocalDataSource): IPlacesLocalDataSource

    @Binds
    @Singleton
    fun bindCategoriesRemoteDataStore(impl: FirebaseCategoriesRemoteDataSource): ICategoriesRemoteDataSource

    @Binds
    @Singleton
    fun bindCategoriesLocalDataStore(impl: CategoriesLocalDataSource): ICategoriesLocalDataSource

    @Binds
    @Singleton
    fun bindUserRemoteDataStore(impl: FirebaseUserRemoteDataSource): IUserRemoteDataSource

    @Binds
    @Singleton
    fun bindUserLocalDataStore(impl: UserLocalDataSource): IUserLocalDataSource
}