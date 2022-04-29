package com.steeshock.streetworkout.domain

import com.steeshock.streetworkout.data.repository.interfaces.IPlacesRepository
import com.steeshock.streetworkout.data.repository.interfaces.IUserRepository
import com.steeshock.streetworkout.services.auth.IAuthService

class FavoritesInteractor(
    private val authService: IAuthService,
    private val placesRepository: IPlacesRepository,
    private val userRepository: IUserRepository,

    ) : IFavoritesInteractor {
    override suspend fun updatePlacesWithUserFavoritesList() {
        if (authService.isUserAuthorized) {
            val userFavorites = userRepository.getUserFavorites(authService.currentUserId)
            placesRepository.updatePlacesWithFavoriteList(userFavorites)
        }
    }
}