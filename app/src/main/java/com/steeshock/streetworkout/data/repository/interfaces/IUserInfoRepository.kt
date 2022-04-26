package com.steeshock.streetworkout.data.repository.interfaces

import com.steeshock.streetworkout.data.model.UserInfo

interface IUserInfoRepository {
    /**
     * Get if exist user info or create new one in remote storage
     *
     * [userId] - uid of created Firebase User
     *
     * @return created UserInfo
     */
    suspend fun getOrCreateUserInfo(userId: String, name: String, email: String): UserInfo?

    /**
     * Get list of favorite places by userId
     */
    suspend fun getUserFavorites(userId: String): List<String>?
}