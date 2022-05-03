package com.steeshock.streetworkout.domain

import com.steeshock.streetworkout.data.model.Place

/**
 * Interactor to update/reset places with favorites list from
 * different places in application
 *
 * The main point: The local data of user favorites have
 * high priority because of offline work with favorites
 */
interface IFavoritesInteractor {
    /**
     * Sync favorite places of authorized user
     *
     * [softSync] - Used soft "additive" merging - if not authorised user already add something in favorites,
     * we keep this data and just merge with fetched user data favorites, after that update
     * user favorite list both locally and remote.
     * If set to False then data will be overridden by fetched remote favorites
     *
     * [reloadUserData] - if true than reload and save user data from remote storage
     * Commonly useful in cases of using app from different devices
     * In this case the server data is the single source of truth
     */
    suspend fun syncFavoritePlaces(softSync: Boolean = true, reloadUserData: Boolean = false)

    /**
     * Update place's favorite state locally and remote (invert current state)
     *
     * [newState] - set favorite state to particular value, not inverted
     */
    suspend fun updatePlaceFavoriteState(place: Place, newState: Boolean? = null)

    /**
     * Reset all favorite places, commonly after logout
     */
    suspend fun resetFavorites()
}