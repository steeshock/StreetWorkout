package com.steeshock.streetworkout.di.modules

import com.steeshock.streetworkout.data.api.ApiUtils
import com.steeshock.streetworkout.data.api.PlacesAPI
import com.steeshock.streetworkout.services.auth.FirebaseAuthService
import com.steeshock.streetworkout.services.auth.IAuthService
import com.steeshock.streetworkout.services.connectivity.ConnectivityService
import com.steeshock.streetworkout.services.connectivity.IConnectivityService
import dagger.Module
import dagger.Provides

@Module
class NetworkModule {

    @Provides
    fun provideConnectivityService(): IConnectivityService {
        return ConnectivityService()
    }

    @Provides
    fun providePlacesApi(): PlacesAPI {
        return ApiUtils.getInstance()
    }

    @Provides
    fun provideAuthService(): IAuthService {
        return FirebaseAuthService()
    }
}