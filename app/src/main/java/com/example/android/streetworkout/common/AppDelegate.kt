package com.example.android.streetworkout.common

import android.app.Application
import com.example.android.streetworkout.data.database.PlacesDatabase
import com.example.android.streetworkout.data.Repository
import com.example.android.streetworkout.utils.InjectorUtils

class AppDelegate : Application() {

    //private var mRepository: Repository? = null

    override fun onCreate() {
        super.onCreate()

        //mRepository = InjectorUtils.getRepository(this)
    }

//    fun getRepository(): Repository? {
//        return mRepository
//    }
}