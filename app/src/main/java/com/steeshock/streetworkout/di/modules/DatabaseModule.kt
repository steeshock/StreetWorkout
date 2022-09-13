package com.steeshock.streetworkout.di.modules

import android.content.Context
import com.steeshock.streetworkout.data.dao.CategoriesDao
import com.steeshock.streetworkout.data.dao.PlacesDao
import com.steeshock.streetworkout.common.PlacesDatabase
import com.steeshock.streetworkout.data.dao.UserDao
import dagger.Module
import dagger.Provides

@Module
object DatabaseModule {
    @Provides
    fun providePlacesDao(appContext: Context): PlacesDao {
        return PlacesDatabase
            .getInstance(appContext)
            .getPlacesDao()
    }

    @Provides
    fun provideCategoriesDao(appContext: Context): CategoriesDao {
        return PlacesDatabase
            .getInstance(appContext)
            .getCategoriesDao()
    }

    @Provides
    fun provideUserDao(appContext: Context): UserDao {
        return PlacesDatabase
            .getInstance(appContext)
            .getUserDao()
    }
}