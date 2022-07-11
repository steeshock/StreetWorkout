package com.steeshock.streetworkout.di.components

import androidx.lifecycle.ViewModel
import com.steeshock.streetworkout.di.common.ViewModelKey
import com.steeshock.streetworkout.presentation.viewmodels.FavoritePlacesViewModel
import com.steeshock.streetworkout.presentation.views.FavoritePlacesFragment
import dagger.Binds
import dagger.Module
import dagger.Subcomponent
import dagger.multibindings.IntoMap

@Module
interface FavoritePlacesModule {
    @[Binds IntoMap ViewModelKey(FavoritePlacesViewModel::class)]
    fun bindFavoritePlacesViewModel(viewModel: FavoritePlacesViewModel): ViewModel
}

@Subcomponent(modules = [FavoritePlacesModule::class])
interface FavoritePlacesComponent {
    fun inject(favoritePlacesFragment: FavoritePlacesFragment)
}