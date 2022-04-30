package com.steeshock.streetworkout.domain

import com.steeshock.streetworkout.data.model.Place

/**
 * Interactor to update/reset places with favorites list from
 * different places in application
 */
interface IFavoritesInteractor {
    /**
     * Update places favorite states with favorites list from authorized User
     *
     * Used "additive" merging - if not authorised user already add something in favorites,
     * we save this data and just merge with fetched user data favorites
     */
    suspend fun updatePlacesWithUserFavoritesList()

    /**
     * Update place's favorite state locally and remote (invert current state)
     *
     * [forceState] - set favorite state to particular value, not inverted
     */
    suspend fun updatePlaceFavoriteState(place: Place, forceState: Boolean? = null)

    /**
     * Reset all favorite places, commonly after logout
     */
    suspend fun resetFavorites()
}