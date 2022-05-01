package com.steeshock.streetworkout.domain

import com.steeshock.streetworkout.data.model.Place

/**
 * Interactor to update/reset places with favorites list from
 * different places in application
 */
interface IFavoritesInteractor {
    /**
     * Sync favorite places with favorite list from authorized User
     *
     * Used soft "additive" merging - if not authorised user already add something in favorites,
     * we save this data and just merge with fetched user data favorites, after that update
     * user favorite list both locally and remote
     */
    suspend fun syncUserFavorites()

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