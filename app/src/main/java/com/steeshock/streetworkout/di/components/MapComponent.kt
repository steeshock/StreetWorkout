package com.steeshock.streetworkout.di.components

import androidx.lifecycle.ViewModel
import com.steeshock.streetworkout.di.common.ViewModelKey
import com.steeshock.streetworkout.presentation.viewmodels.MapViewModel
import com.steeshock.streetworkout.presentation.views.MapFragment
import dagger.Binds
import dagger.Module
import dagger.Subcomponent
import dagger.multibindings.IntoMap

@Module
interface MapModule {
    @[Binds IntoMap ViewModelKey(MapViewModel::class)]
    fun bindMapViewModel(viewModel: MapViewModel): ViewModel
}

@Subcomponent(modules = [MapModule::class])
interface MapComponent {
    fun inject(mapFragment: MapFragment)
}