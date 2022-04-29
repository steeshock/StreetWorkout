package com.steeshock.streetworkout.domain

/**
 * Interactor to update places with favorites list from
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
}