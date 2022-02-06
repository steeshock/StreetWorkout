package com.steeshock.android.streetworkout.di.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Module
import dagger.Provides
import javax.inject.Provider

@Module
abstract class ViewModelModule {
    companion object {
        @Provides
        fun viewModelFactory(providerMap: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>): ViewModelProvider.Factory =
            ViewModelFactory(providerMap)
    }
}