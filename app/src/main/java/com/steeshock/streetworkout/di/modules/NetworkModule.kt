package com.steeshock.streetworkout.di.modules

import com.steeshock.streetworkout.data.api.ApiUtils
import com.steeshock.streetworkout.data.api.PlacesAPI
import com.steeshock.streetworkout.data.services.auth.FirebaseAuthService
import com.steeshock.streetworkout.data.services.connectivity.ConnectivityService
import com.steeshock.streetworkout.data.services.connectivity.IConnectivityService
import com.steeshock.streetworkout.domain.repository.IAuthService
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
interface NetworkModule {
    @Binds
    fun bindConnectivityService(connectivityService: ConnectivityService): IConnectivityService

    @Binds
    fun bindAuthService(authService: FirebaseAuthService): IAuthService

    companion object {
        @Provides
        @Singleton
        fun providePlacesApi(): PlacesAPI {
            return ApiUtils.getInstance()
        }
    }
}