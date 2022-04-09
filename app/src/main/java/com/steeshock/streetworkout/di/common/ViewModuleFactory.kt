package com.steeshock.streetworkout.di.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Provider
import javax.inject.Singleton

/**
 * Use this factory in Dagger components:
 *
 * @[Binds IntoMap ViewModelKey(MyViewModel::class)]
 * abstract fun bindMyViewModel(myViewModel: MyViewModel): ViewModel
 */
@Singleton
class ViewModelFactory(
    private val providers: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val provider =
            providers[modelClass] ?: providers.asIterable()
                .firstOrNull { modelClass.isAssignableFrom(it.key) }?.value
            ?: throw IllegalArgumentException("Unknown ViewModel class: $modelClass")

        @Suppress("UNCHECKED_CAST")
        try {
            return provider.get() as T
        } catch (ex: Exception) {
            throw RuntimeException(ex)
        }
    }
}