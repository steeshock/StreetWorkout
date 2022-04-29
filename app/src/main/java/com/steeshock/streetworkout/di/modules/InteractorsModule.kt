package com.steeshock.streetworkout.di.modules

import com.steeshock.streetworkout.data.api.ApiUtils
import com.steeshock.streetworkout.data.api.PlacesAPI
import com.steeshock.streetworkout.services.auth.FirebaseAuthServiceImpl
import com.steeshock.streetworkout.services.auth.IAuthService
import dagger.Module
import dagger.Provides

@Module
class NetworkModule {

    @Provides
    fun providePlacesApi(): PlacesAPI {
        return ApiUtils.getInstance()
    }

    @Provides
    fun provideAuthService(): IAuthService {
        return FirebaseAuthServiceImpl()
    }
}