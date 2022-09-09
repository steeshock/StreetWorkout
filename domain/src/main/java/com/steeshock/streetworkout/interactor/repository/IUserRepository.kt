package com.steeshock.streetworkout.interactor.repository

import com.steeshock.streetworkout.interactor.entity.User

interface IUserRepository {
    /**
     * Fetch if exist user info or create new one in remote storage
     * After user created, add it to local storage (update user locally if exists)
     *
     * [signedUser] - signed Firebase User
     *
     * @return created User
     */
    suspend fun getOrCreateUser(signedUser: User): User?

    /**
     * Get list of favorite places by userId
     */
    suspend fun getUserFavorites(userId: String): List<String>

    /**
     * Update whole user's favorite list or Add/remove place with placeId both locally and remote
     */
    suspend fun updateUserFavoriteList(
        userId: String,
        favorites: List<String>? = null,
        favoritePlaceId: String? = null,
    )

    /**
     * Sync user with remote data
     */
    suspend fun syncUser(userId: String)
}