package com.steeshock.streetworkout.data.dataSources.interfaces.local

import com.steeshock.streetworkout.data.model.dto.UserDto

interface IUserLocalDataSource {

    /**
     * Returns user by id from local storage
     */
    suspend fun getUserById(userId: String): UserDto?

    /**
     * Update user in User table
     */
    suspend fun updateUserLocal(userDto: UserDto)

    /**
     * Insert new user in Local User storage
     */
    suspend fun insertUserLocal(userDto: UserDto)

    /**
     * Update user with favorites list locally
     */
    suspend fun updateUserFavoritesLocally(
        localUserDto: UserDto,
        favorites: List<String>? = null,
        favoritePlaceId: String? = null,
    ): List<String>
}