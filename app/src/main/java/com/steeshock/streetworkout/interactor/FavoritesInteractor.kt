package com.steeshock.streetworkout.interactor

import com.steeshock.streetworkout.data.workers.SyncFavoritesWorker.Companion.SYNC_FAVORITES_WORK
import com.steeshock.streetworkout.data.workers.SyncFavoritesWorker.Companion.SyncFavoritesException
import com.steeshock.streetworkout.data.workers.common.IWorkerService
import com.steeshock.streetworkout.interactor.entity.Place
import com.steeshock.streetworkout.interactor.interactor.IFavoritesInteractor
import com.steeshock.streetworkout.interactor.repository.IAuthService
import com.steeshock.streetworkout.interactor.repository.IPlacesRepository
import com.steeshock.streetworkout.interactor.repository.IUserRepository
import javax.inject.Inject

class FavoritesInteractor @Inject constructor(
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

    override suspend fun updatePlaceFavoriteState(placeDto: Place, newState: Boolean?) {
        when (newState) {
            null -> {
                placesRepository.updatePlace(placeDto.copy(isFavorite = !placeDto.isFavorite))
            }
            else -> {
                placesRepository.updatePlace(placeDto.copy(isFavorite = newState))
            }
        }
        if (authService.isUserAuthorized) {
            userRepository.updateUserFavoriteList(authService.currentUserId, favoritePlaceId = placeDto.placeId)
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