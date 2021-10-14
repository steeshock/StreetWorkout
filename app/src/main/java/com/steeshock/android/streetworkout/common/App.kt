package com.steeshock.android.streetworkout.common

import android.app.Application
import android.content.Context
import com.steeshock.android.streetworkout.di.AppComponent
import com.steeshock.android.streetworkout.di.DaggerAppComponent
import com.steeshock.android.streetworkout.di.modules.DatabaseModule

class App : Application() {

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerAppComponent
            .builder()
            .databaseModule(DatabaseModule(this))
            .build()
    }
}

val Context.appComponent: AppComponent
    get() = when (this) {
        is App -> appComponent
        else -> this.applicationContext.appComponent
    }