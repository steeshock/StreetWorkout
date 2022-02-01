package com.steeshock.android.streetworkout.di.components

import androidx.lifecycle.ViewModel
import com.steeshock.android.streetworkout.di.common.ViewModelKey
import com.steeshock.android.streetworkout.presentation.viewmodels.MapViewModel
import com.steeshock.android.streetworkout.presentation.views.MapFragment
import dagger.Binds
import dagger.Module
import dagger.Subcomponent
import dagger.multibindings.IntoMap

@Module
abstract class MapModule {
    @[Binds IntoMap ViewModelKey(MapViewModel::class)]
    abstract fun bindMapViewModel(viewModel: MapViewModel): ViewModel
}

@Subcomponent(modules = [MapModule::class])
interface MapComponent {
    fun inject(mapFragment: MapFragment)
}