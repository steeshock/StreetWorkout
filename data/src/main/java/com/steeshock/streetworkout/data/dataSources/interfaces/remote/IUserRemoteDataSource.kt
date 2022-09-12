package com.steeshock.streetworkout.data.dataSources.interfaces.remote

import com.steeshock.streetworkout.data.repository.dto.UserDto

/**
 * Implementation for Firebase Realtime Database
 */
interface IUserRemoteDataSource {
    /**
     * Fetch if exist user info from remote storage
     */
    suspend fun getUserRemote(userId: String): UserDto?

    /**
     * Create new user info in remote storage
     */
    suspend fun createUserRemote(userId: String, displayName: String, email: String): UserDto
}