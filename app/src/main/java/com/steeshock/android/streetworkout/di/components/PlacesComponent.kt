package com.steeshock.android.streetworkout.di.components

import androidx.lifecycle.ViewModel
import com.steeshock.android.streetworkout.di.common.ViewModelKey
import com.steeshock.android.streetworkout.presentation.viewmodels.AddPlaceViewModel
import com.steeshock.android.streetworkout.presentation.viewmodels.PlacesViewModel
import com.steeshock.android.streetworkout.presentation.views.AddPlaceFragment
import com.steeshock.android.streetworkout.presentation.views.PlacesFragment
import dagger.Binds
import dagger.Module
import dagger.Subcomponent
import dagger.multibindings.IntoMap

@Module
abstract class PlacesModule {
    @[Binds IntoMap ViewModelKey(PlacesViewModel::class)]
    abstract fun bindPlacesViewModel(viewModel: PlacesViewModel): ViewModel

    @[Binds IntoMap ViewModelKey(AddPlaceViewModel::class)]
    abstract fun bindAddPlaceViewModel(viewModel: AddPlaceViewModel): ViewModel
}

@Subcomponent(modules = [PlacesModule::class])
interface PlacesComponent {
    fun inject(placesFragment: PlacesFragment)

    fun inject(addPlaceFragment: AddPlaceFragment)
}