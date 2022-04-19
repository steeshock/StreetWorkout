package com.steeshock.streetworkout.common

import android.app.Application
import android.content.Context
import com.steeshock.streetworkout.di.AppComponent
import com.steeshock.streetworkout.di.DaggerAppComponent
import com.steeshock.streetworkout.di.modules.ApplicationModule
import com.steeshock.streetworkout.di.modules.DatabaseModule

class App : Application() {

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerAppComponent
            .builder()
            .applicationModule(ApplicationModule(this))
            .build()
    }
}

val Context.appComponent: AppComponent
    get() = when (this) {
        is App -> appComponent
        else -> this.applicationContext.appComponent
    }