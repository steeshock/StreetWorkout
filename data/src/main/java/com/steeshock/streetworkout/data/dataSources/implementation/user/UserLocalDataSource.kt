package com.steeshock.streetworkout.data.dataSources.implementation.user

import com.steeshock.streetworkout.data.dataSources.interfaces.local.IUserLocalDataSource
import com.steeshock.streetworkout.data.dao.UserDao
import com.steeshock.streetworkout.data.model.dto.UserDto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class UserLocalDataSource @Inject constructor(
    private val userDao: UserDao,
) : IUserLocalDataSource {

    override suspend fun getUserById(userId: String): UserDto? {
        return userDao.getUserById(userId)
    }

    override suspend fun updateUserLocal(userDto: UserDto) {
        userDao.updateUser(userDto)
    }

    override suspend fun insertUserLocal(userDto: UserDto) {
        userDao.insertUser(userDto)
    }

    override suspend fun updateUserFavoritesLocally(
        localUserDto: UserDto,
        favorites: List<String>?,
        favoritePlaceId: String?,
    ): List<String> {
        return suspendCoroutine { continuation ->
            val newFavorites = arrayListOf<String>().apply {
                when {
                    favoritePlaceId != null -> {
                        val userFavorites = localUserDto.favorites?.toMutableList() ?: mutableListOf()
                        when {
                            userFavorites.contains(favoritePlaceId) -> {
                                userFavorites.remove(favoritePlaceId)
                            }
                            else -> {
                                userFavorites.add(favoritePlaceId)
                            }
                        }
                        addAll(userFavorites)
                    }
                    favorites != null -> {
                        addAll(favorites)
                    }
                }
            }
            CoroutineScope(Dispatchers.IO).launch {
                updateUserLocal(localUserDto.copy(favorites = newFavorites))
            }.invokeOnCompletion {
                continuation.resume(newFavorites)
            }
        }
    }
}