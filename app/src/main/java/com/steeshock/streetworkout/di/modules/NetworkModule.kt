package com.steeshock.streetworkout.di.modules

import com.steeshock.streetworkout.data.api.ApiUtils
import com.steeshock.streetworkout.data.api.PlacesAPI
import com.steeshock.streetworkout.services.auth.FirebaseAuthService
import com.steeshock.streetworkout.services.auth.IAuthService
import com.steeshock.streetworkout.services.connectivity.ConnectivityService
import com.steeshock.streetworkout.services.connectivity.IConnectivityService
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
interface NetworkModule {

    @Binds
    fun bindConnectivityService(connectivityService: ConnectivityService): IConnectivityService

    @Binds
    fun bindAuthService(authService: FirebaseAuthService): IAuthService

    companion object {
        @Provides
        fun providePlacesApi(): PlacesAPI {
            return ApiUtils.getInstance()
        }
    }
}