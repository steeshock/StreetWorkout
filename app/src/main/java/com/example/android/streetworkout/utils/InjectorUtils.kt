package com.example.android.streetworkout.utils

import android.content.Context
import androidx.fragment.app.Fragment
import com.example.android.streetworkout.data.Repository
import com.example.android.streetworkout.data.database.PlacesDatabase

object InjectorUtils {

    fun getRepository(context: Context): Repository {
        return Repository.getInstance(
            PlacesDatabase.getInstance(context.applicationContext).getPlacesDao())
    }
}