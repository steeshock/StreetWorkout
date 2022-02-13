package com.steeshock.android.streetworkout.di.components

import androidx.lifecycle.ViewModel
import com.steeshock.android.streetworkout.di.common.ViewModelKey
import com.steeshock.android.streetworkout.presentation.viewmodels.AuthViewModel
import com.steeshock.android.streetworkout.presentation.views.AuthActivity
import dagger.Binds
import dagger.Module
import dagger.Subcomponent
import dagger.multibindings.IntoMap

@Module
abstract class AuthModule {
    @[Binds IntoMap ViewModelKey(AuthViewModel::class)]
    abstract fun bindAuthViewModel(viewModel: AuthViewModel): ViewModel
}

@Subcomponent(modules = [AuthModule::class])
interface AuthComponent {
    fun inject(authActivity: AuthActivity)
}