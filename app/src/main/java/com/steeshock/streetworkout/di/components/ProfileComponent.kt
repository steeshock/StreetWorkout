package com.steeshock.streetworkout.di.components

import androidx.lifecycle.ViewModel
import com.steeshock.streetworkout.di.common.ViewModelKey
import com.steeshock.streetworkout.presentation.viewmodels.ProfileViewModel
import com.steeshock.streetworkout.presentation.views.ProfileFragment
import dagger.Binds
import dagger.Module
import dagger.Subcomponent
import dagger.multibindings.IntoMap

@Module
interface ProfileModule {
    @[Binds IntoMap ViewModelKey(ProfileViewModel::class)]
    fun bindProfileViewModel(viewModel: ProfileViewModel): ViewModel
}

@Subcomponent(modules = [ProfileModule::class])
interface ProfileComponent {
    fun inject(profileFragment: ProfileFragment)
}