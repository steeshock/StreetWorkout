package com.steeshock.android.streetworkout.di

import com.steeshock.android.streetworkout.di.modules.DatabaseModule
import com.steeshock.android.streetworkout.di.modules.NetworkModule
import com.steeshock.android.streetworkout.views.*
import dagger.Component

@Component(modules = [
    DatabaseModule::class,
    NetworkModule::class,
])
interface AppComponent {

    fun inject(placesFragment: PlacesFragment)

    fun inject(mapFragment: MapFragment)

    fun inject(favoritePlacesFragment: FavoritePlacesFragment)

    fun inject(profileFragment: ProfileFragment)

    fun inject(addPlaceFragment: AddPlaceFragment)
}