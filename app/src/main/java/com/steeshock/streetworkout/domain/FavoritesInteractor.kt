package com.steeshock.streetworkout.domain

import com.steeshock.streetworkout.data.model.Place
import com.steeshock.streetworkout.data.repository.interfaces.IPlacesRepository
import com.steeshock.streetworkout.data.repository.interfaces.IUserRepository
import com.steeshock.streetworkout.services.auth.IAuthService

class FavoritesInteractor(
    private val authService: IAuthService,
    private val placesRepository: IPlacesRepository,
    private val userRepository: IUserRepository,

    ) : IFavoritesInteractor {
    override suspend fun syncUserFavorites() {
        if (authService.isUserAuthorized) {
            val remoteUserFavorites = userRepository.getUserFavorites(authService.currentUserId)
            val localUserFavorites = placesRepository.getLocalFavorites()
            val mergedFavorites = getMergedFavorites(remoteUserFavorites, localUserFavorites).toList()

            if (remoteUserFavorites != mergedFavorites) {
                userRepository.updateUserFavoriteList(authService.currentUserId, mergedFavorites)
            }

            placesRepository.updatePlacesWithFavoriteList(mergedFavorites)
        }
    }

    override suspend fun updatePlaceFavoriteState(place: Place, forceState: Boolean?) {
        when (forceState) {
            null -> {
                placesRepository.updatePlace(place.copy(isFavorite = !place.isFavorite))
            }
            else -> {
                placesRepository.updatePlace(place.copy(isFavorite = forceState))
            }
        }
        if (authService.isUserAuthorized) {
            userRepository.addPlaceToUserFavoriteList(authService.currentUserId, place.placeId)
        }
    }

    override suspend fun resetFavorites() {
        placesRepository.resetFavorites()
    }

    private fun getMergedFavorites(remoteUserFavorites: List<String>, localUserFavorites: List<String>) =
        mutableSetOf<String>().apply {
            addAll(remoteUserFavorites)
            addAll(localUserFavorites)
        }
}