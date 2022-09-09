package com.steeshock.streetworkout.di.modules

import com.steeshock.streetworkout.data.repository.implementation.DataStoreRepository
import com.steeshock.streetworkout.data.repository.implementation.firebase.FirebaseCategoriesRepository
import com.steeshock.streetworkout.data.repository.implementation.firebase.FirebasePlacesRepository
import com.steeshock.streetworkout.data.repository.implementation.firebase.FirebaseUserRepository
import com.steeshock.streetworkout.domain.repository.ICategoriesRepository
import com.steeshock.streetworkout.data.repository.interfaces.IDataStoreRepository
import com.steeshock.streetworkout.data.repository.interfaces.IPlacesRepository
import com.steeshock.streetworkout.domain.repository.IUserRepository
import dagger.Binds
import dagger.Module
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
    //fun bindCategoriesRepository(categoriesRepository: SimpleApiCategoriesRepository): ICategoriesRepository

    @Binds
    @Singleton
    fun bindUserRepository(userRepository: FirebaseUserRepository): IUserRepository

    @Binds
    @Singleton
    fun bindDataStoreRepository(dataStoreRepository: DataStoreRepository): IDataStoreRepository

}