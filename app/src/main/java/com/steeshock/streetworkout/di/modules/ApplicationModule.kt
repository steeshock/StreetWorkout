package com.steeshock.streetworkout.di.modules

import android.content.Context
import com.steeshock.streetworkout.data.api.ApiUtils
import com.steeshock.streetworkout.data.api.PlacesAPI
import com.steeshock.streetworkout.services.auth.FirebaseAuthServiceImpl
import com.steeshock.streetworkout.services.auth.IAuthService
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ApplicationModule(private val appContext: Context)  {

    @Provides
    @Singleton
    fun provideAppContext(): Context {
        return appContext
    }
}