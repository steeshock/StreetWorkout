package com.steeshock.streetworkout.di.modules

import com.steeshock.streetworkout.domain.favorites.FavoritesInteractor
import com.steeshock.streetworkout.domain.favorites.IFavoritesInteractor
import com.steeshock.streetworkout.domain.login.ILoginInteractor
import com.steeshock.streetworkout.domain.login.LoginInteractor
import dagger.Binds
import dagger.Module

@Module
interface InteractorsModule {
    @Binds
    fun bindFavoritesInteractor(favoritesInteractor: FavoritesInteractor): IFavoritesInteractor

    @Binds
    fun bindLoginInteractor(loginInteractor: LoginInteractor): ILoginInteractor
}