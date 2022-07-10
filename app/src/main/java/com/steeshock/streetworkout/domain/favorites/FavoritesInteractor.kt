package com.steeshock.streetworkout.domain.favorites

import com.steeshock.streetworkout.data.model.Place
import com.steeshock.streetworkout.data.repository.interfaces.IPlacesRepository
import com.steeshock.streetworkout.data.repository.interfaces.IUserRepository
import com.steeshock.streetworkout.data.workers.common.IWorkerService
import com.steeshock.streetworkout.data.workers.SyncFavoritesWorker.Companion.SYNC_FAVORITES_WORK
import com.steeshock.streetworkout.data.workers.SyncFavoritesWorker.Companion.SyncFavoritesException
import com.steeshock.streetworkout.services.auth.IAuthService

class FavoritesInteractor(
    private val authService: IAuthService,
    private val placesRepository: IPlacesRepository,
    private val userRepository: IUserRepository,
    private val workerService: IWorkerService,
) : IFavoritesInteractor {
    override suspend fun syncFavoritePlaces(softSync: Boolean, reloadUserData: Boolean) {

        if (!workerService.isUniqueWorkDone(SYNC_FAVORITES_WORK)) {
            throw SyncFavoritesException()
        }

        if (authService.isUserAuthorized) {

            if (reloadUserData) {
                userRepository.syncUser(authService.currentUserId)
            }

            val remoteUserFavorites = userRepository.getUserFavorites(authService.currentUserId).sorted()
            val localUserFavorites = placesRepository.getLocalFavorites().sorted()

            when (softSync) {
                true -> {
                    val mergedFavorites = getMergedFavorites(remoteUserFavorites, localUserFavorites).toList().sorted()
                    if (remoteUserFavorites != mergedFavorites) {
                        userRepository.updateUserFavoriteList(authService.currentUserId, favorites = mergedFavorites)
                    }
                    placesRepository.updatePlacesWithFavoriteList(mergedFavorites)
                }
                false -> {
                    if (remoteUserFavorites != localUserFavorites) {
                        placesRepository.updatePlacesWithFavoriteList(remoteUserFavorites)
                    }
                }
            }
        }
    }

    override suspend fun updatePlaceFavoriteState(place: Place, newState: Boolean?) {
        when (newState) {
            null -> {
                placesRepository.updatePlace(place.copy(isFavorite = !place.isFavorite))
            }
            else -> {
                placesRepository.updatePlace(place.copy(isFavorite = newState))
            }
        }
        if (authService.isUserAuthorized) {
            userRepository.updateUserFavoriteList(authService.currentUserId, favoritePlaceId = place.placeId)
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