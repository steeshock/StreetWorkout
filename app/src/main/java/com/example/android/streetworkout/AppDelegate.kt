package com.example.android.streetworkout

import android.app.Application
import com.example.android.streetworkout.data.database.PlacesDatabase
import com.example.android.streetworkout.data.Repository

class AppDelegate : Application() {

    private var mRepository: Repository? = null
    private var mDatabase: PlacesDatabase? = null

    override fun onCreate() {
        super.onCreate()

        mDatabase = PlacesDatabase.getInstance(this)
        mRepository = Repository.getInstance(mDatabase!!.getPlacesDao())
    }

    fun getRepository(): Repository? {
        return mRepository
    }
}