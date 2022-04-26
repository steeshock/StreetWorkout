package com.steeshock.streetworkout.data.repository.interfaces

import com.steeshock.streetworkout.data.model.User

interface IUserInfoRepository {
    /**
     * Get if exist user info or create new one in remote storage
     *
     * [userId] - uid of created Firebase User
     *
     * @return created User
     */
    suspend fun getOrCreateUserInfo(userId: String, name: String, email: String): User?
}