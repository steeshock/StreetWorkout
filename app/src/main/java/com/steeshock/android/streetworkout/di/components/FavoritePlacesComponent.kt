package com.steeshock.android.streetworkout.di.components

import androidx.lifecycle.ViewModel
import com.steeshock.android.streetworkout.di.common.ViewModelKey
import com.steeshock.android.streetworkout.presentation.viewmodels.FavoritePlacesViewModel
import com.steeshock.android.streetworkout.presentation.views.FavoritePlacesFragment
import dagger.Binds
import dagger.Module
import dagger.Subcomponent
import dagger.multibindings.IntoMap

@Module
abstract class FavoritePlacesModule {
    @[Binds IntoMap ViewModelKey(FavoritePlacesViewModel::class)]
    abstract fun bindFavoritePlacesViewModel(viewModel: FavoritePlacesViewModel): ViewModel
}

@Subcomponent(modules = [FavoritePlacesModule::class])
interface FavoritePlacesComponent {
    fun inject(favoritePlacesFragment: FavoritePlacesFragment)
}