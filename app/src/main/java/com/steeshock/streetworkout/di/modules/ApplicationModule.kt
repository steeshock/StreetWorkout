package com.steeshock.streetworkout.di.modules

import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(includes = [WorkerModule::class])
class ApplicationModule(private val appContext: Context) {
    @Provides
    @Singleton
    fun provideAppContext(): Context {
        return appContext
    }
}