package com.steeshock.streetworkout.data.repository.implementation.firebase

import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.steeshock.streetworkout.common.Constants
import com.steeshock.streetworkout.data.database.UserDao
import com.steeshock.streetworkout.data.model.User
import com.steeshock.streetworkout.data.repository.interfaces.IUserRepository
import com.steeshock.streetworkout.data.workers.common.IWorkerService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class FirebaseUserRepository @Inject constructor(
    private val userDao: UserDao,
    private val workerService: IWorkerService,
) : IUserRepository {

    override suspend fun getOrCreateUser(signedUser: User): User? {
        return fetchUser(signedUser.userId) ?: createUser(signedUser.userId,
            signedUser.displayName,
            signedUser.email)
    }

    override suspend fun getUserFavorites(userId: String): List<String> {
        return userDao.getUserById(userId)?.favorites ?: listOf()
    }

    /**
     * Request send updated favorites only when internet is available
     * Using WorkManager to guarantee work is done for offline mode
     */
    override suspend fun updateUserFavoriteList(
        userId: String,
        favorites: List<String>?,
        favoritePlaceId: String?,
    ) {
        userDao.getUserById(userId)?.let { localUser ->
            val locallyFavorites = updateUserFavoritesLocally(localUser, favorites, favoritePlaceId)
            workerService.syncFavorites(localUser.userId, locallyFavorites)
        }
    }

    override suspend fun syncUser(userId: String) {
        fetchUser(userId)
    }

    private suspend fun updateUserFavoritesLocally(
        localUser: User,
        favorites: List<String>? = null,
        favoritePlaceId: String? = null,
    ): List<String> {
        return suspendCoroutine { continuation ->
            val newFavorites = arrayListOf<String>().apply {
                when {
                    favoritePlaceId != null -> {
                        val userFavorites = localUser.favorites?.toMutableList() ?: mutableListOf()
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
                userDao.updateUser(localUser.copy(favorites = newFavorites))
            }.invokeOnCompletion {
                continuation.resume(newFavorites)
            }
        }
    }

    private suspend fun createUser(userId: String, displayName: String, email: String): User? {
        return suspendCoroutine { continuation ->
            val database = Firebase.database(Constants.FIREBASE_PATH)
            val newUserRef = database.getReference("users").child(userId)
            val user = User(userId, displayName, email)
            newUserRef.setValue(user)
                .addOnSuccessListener {
                    CoroutineScope(Dispatchers.IO).launch {
                        userDao.insertUser(user)
                    }.invokeOnCompletion {
                        continuation.resume(user)
                    }
                }
                .addOnFailureListener {
                    continuation.resumeWithException(it)
                }
        }
    }

    private suspend fun fetchUser(userId: String): User? {
        return suspendCoroutine { continuation ->
            val database = Firebase.database(Constants.FIREBASE_PATH)
            val userByIdRef = database.getReference("users").child(userId)
            userByIdRef.get()
                .addOnSuccessListener { snapshot ->
                    when (val existentUser = snapshot.getValue<User>()) {
                        null -> {
                            continuation.resume(existentUser)
                        }
                        else -> {
                            CoroutineScope(Dispatchers.IO).launch {
                                userDao.insertUser(existentUser)
                            }.invokeOnCompletion {
                                continuation.resume(existentUser)
                            }
                        }
                    }
                }
                .addOnFailureListener {
                    continuation.resumeWithException(it)
                }
        }
    }
}