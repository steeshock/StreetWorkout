package com.steeshock.streetworkout.di.modules

import com.steeshock.streetworkout.domain.FavoritesInteractor
import com.steeshock.streetworkout.domain.interactor.IFavoritesInteractor
import com.steeshock.streetworkout.domain.interactor.ILoginInteractor
import com.steeshock.streetworkout.domain.LoginInteractor
import dagger.Binds
import dagger.Module

@Module
interface InteractorsModule {
    @Binds
    fun bindFavoritesInteractor(favoritesInteractor: FavoritesInteractor): IFavoritesInteractor

    @Binds
    fun bindLoginInteractor(loginInteractor: LoginInteractor): ILoginInteractor
}