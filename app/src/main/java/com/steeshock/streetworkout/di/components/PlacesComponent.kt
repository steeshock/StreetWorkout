package com.steeshock.streetworkout.di.components

import androidx.lifecycle.ViewModel
import com.steeshock.streetworkout.di.common.ViewModelKey
import com.steeshock.streetworkout.presentation.viewmodels.AddPlaceViewModel
import com.steeshock.streetworkout.presentation.viewmodels.PlacesViewModel
import com.steeshock.streetworkout.presentation.views.AddPlaceFragment
import com.steeshock.streetworkout.presentation.views.PlacesFragment
import dagger.Binds
import dagger.Module
import dagger.Subcomponent
import dagger.multibindings.IntoMap

@Module
interface PlacesModule {
    @[Binds IntoMap ViewModelKey(PlacesViewModel::class)]
    fun bindPlacesViewModel(viewModel: PlacesViewModel): ViewModel

    @[Binds IntoMap ViewModelKey(AddPlaceViewModel::class)]
    fun bindAddPlaceViewModel(viewModel: AddPlaceViewModel): ViewModel
}

@Subcomponent(modules = [PlacesModule::class])
interface PlacesComponent {
    fun inject(placesFragment: PlacesFragment)

    fun inject(addPlaceFragment: AddPlaceFragment)
}