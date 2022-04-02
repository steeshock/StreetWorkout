package com.steeshock.android.streetworkout.di.modules

import com.steeshock.android.streetworkout.data.api.ApiUtils
import com.steeshock.android.streetworkout.data.api.PlacesAPI
import com.steeshock.android.streetworkout.services.auth.FirebaseAuthServiceImpl
import com.steeshock.android.streetworkout.services.auth.IAuthService
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