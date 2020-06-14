package com.example.android.streetworkout

import android.app.Application
import com.example.android.streetworkout.model.PlacesDatabase
import com.example.android.streetworkout.model.Storage

class AppDelegate : Application() {

    private var mStorage: Storage? = null

    override fun onCreate() {
        super.onCreate()

        var database: PlacesDatabase = PlacesDatabase.getInstance(this)

        mStorage = Storage(database.placesDao())
    }

    fun getStorage(): Storage? {
        return mStorage
    }
}