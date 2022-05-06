package com.steeshock.streetworkout.di.modules

import com.steeshock.streetworkout.data.repository.interfaces.IPlacesRepository
import com.steeshock.streetworkout.data.repository.interfaces.IUserRepository
import com.steeshock.streetworkout.domain.FavoritesInteractor
import com.steeshock.streetworkout.domain.IFavoritesInteractor
import com.steeshock.streetworkout.domain.ILoginInteractor
import com.steeshock.streetworkout.domain.LoginInteractor
import com.steeshock.streetworkout.services.auth.IAuthService
import dagger.Module
import dagger.Provides

@Module
class InteractorsModule {

    @Provides
    fun provideFavoritesInteractor(
        authService: IAuthService,
        placesRepository: IPlacesRepository,
        userRepository: IUserRepository,
    ): IFavoritesInteractor {
        return FavoritesInteractor(
            authService,
            placesRepository,
            userRepository,
        )
    }

    @Provides
    fun provideLoginInteractor(
        authService: IAuthService,
        userRepository: IUserRepository,
    ): ILoginInteractor {
        return LoginInteractor(
            authService,
            userRepository,
        )
    }
}