package com.steeshock.streetworkout.data.repository.implementation

import com.steeshock.streetworkout.data.dataSources.interfaces.local.IUserLocalDataSource
import com.steeshock.streetworkout.data.dataSources.interfaces.remote.IUserRemoteDataSource
import com.steeshock.streetworkout.data.mappers.mapToEntity
import com.steeshock.streetworkout.data.workers.common.IWorkerService
import com.steeshock.streetworkout.domain.entity.User
import com.steeshock.streetworkout.domain.repository.IUserRepository
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val userLocalDataSource: IUserLocalDataSource,
    private val userRemoteDataSource: IUserRemoteDataSource,
    private val workerService: IWorkerService,
) : IUserRepository {

    override suspend fun getOrCreateUser(signedUser: User): User {
        return fetchUser(signedUser.userId) ?: createUser(signedUser.userId,
            signedUser.displayName,
            signedUser.email)
    }

    override suspend fun getUserFavorites(userId: String): List<String> {
        return userLocalDataSource.getUserById(userId)?.favorites ?: listOf()
    }

    override suspend fun updateUserFavoriteList(
        userId: String,
        favorites: List<String>?,
        favoritePlaceId: String?,
    ) {
        userLocalDataSource.getUserById(userId)?.let { localUser ->
            val localFavorites =
                userLocalDataSource.updateUserFavoritesLocally(
                    localUserDto = localUser,
                    favorites = favorites,
                    favoritePlaceId = favoritePlaceId,
                )
            workerService.syncFavorites(localUser.userId, localFavorites)
        }
    }

    override suspend fun syncUser(userId: String) {
        fetchUser(userId)
    }

    private suspend fun createUser(userId: String, displayName: String, email: String): User {
        val newUser = userRemoteDataSource.createUserRemote(userId, displayName, email)
        userLocalDataSource.insertUserLocal(newUser)
        return newUser.mapToEntity()
    }

    private suspend fun fetchUser(userId: String): User? {
        val existentUser = userRemoteDataSource.getUserRemote(userId)
        if (existentUser != null) {
            userLocalDataSource.insertUserLocal(existentUser)
        }
        return existentUser?.mapToEntity()
    }
}