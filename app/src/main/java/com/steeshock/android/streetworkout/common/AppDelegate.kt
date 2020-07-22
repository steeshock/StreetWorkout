package com.steeshock.android.streetworkout.common

import android.app.Application

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