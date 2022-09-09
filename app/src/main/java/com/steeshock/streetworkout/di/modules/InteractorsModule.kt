package com.steeshock.streetworkout.di.modules

import com.steeshock.streetworkout.interactor.FavoritesInteractor
import com.steeshock.streetworkout.interactor.interactor.IFavoritesInteractor
import com.steeshock.streetworkout.interactor.interactor.ILoginInteractor
import com.steeshock.streetworkout.interactor.LoginInteractor
import dagger.Binds
import dagger.Module

@Module
interface InteractorsModule {
    @Binds
    fun bindFavoritesInteractor(favoritesInteractor: FavoritesInteractor): IFavoritesInteractor

    @Binds
    fun bindLoginInteractor(loginInteractor: LoginInteractor): ILoginInteractor
}