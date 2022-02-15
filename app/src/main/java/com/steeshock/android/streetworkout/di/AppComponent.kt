package com.steeshock.android.streetworkout.di

import com.steeshock.android.streetworkout.di.common.ViewModelModule
import com.steeshock.android.streetworkout.di.components.FavoritePlacesComponent
import com.steeshock.android.streetworkout.di.components.MapComponent
import com.steeshock.android.streetworkout.di.components.PlacesComponent
import com.steeshock.android.streetworkout.di.components.ProfileComponent
import com.steeshock.android.streetworkout.di.modules.DatabaseModule
import com.steeshock.android.streetworkout.di.modules.NetworkModule
import dagger.Component
import javax.inject.Singleton

@Component(modules = [
    DatabaseModule::class,
    NetworkModule::class,
    ViewModelModule::class,
])
@Singleton
interface AppComponent {

    fun providePlacesComponent(): PlacesComponent

    fun provideMapComponent(): MapComponent

    fun provideFavoritePlacesComponent(): FavoritePlacesComponent

    fun provideProfileComponent(): ProfileComponent
}