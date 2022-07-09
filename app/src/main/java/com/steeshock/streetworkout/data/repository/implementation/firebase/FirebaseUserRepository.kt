package com.steeshock.streetworkout.data.repository.implementation.firebase

import androidx.work.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.steeshock.streetworkout.common.Constants
import com.steeshock.streetworkout.data.database.UserDao
import com.steeshock.streetworkout.data.model.User
import com.steeshock.streetworkout.data.repository.interfaces.IUserRepository
import com.steeshock.streetworkout.data.workers.SyncFavoritesWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class FirebaseUserRepository(
    private val userDao: UserDao,
    private val workManager: WorkManager,
) : IUserRepository {

    override suspend fun getOrCreateUser(signedUser: User): User? {
        return fetchUser(signedUser.userId) ?: createUser(signedUser.userId,
            signedUser.displayName,
            signedUser.email)
    }

    override suspend fun getUserFavorites(userId: String): List<String> {
        return userDao.getUserById(userId)?.favorites ?: listOf()
    }

    override suspend fun updateUserFavoriteList(
        userId: String,
        favorites: List<String>?,
        favoritePlaceId: String?,
    ) {
        userDao.getUserById(userId)?.let { localUser ->
            val locallyFavorites = updateUserFavoritesLocally(localUser, favorites, favoritePlaceId)
            updateUserFavoritesRemote(localUser, locallyFavorites)
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

    /**
     * Request send only when internet is available
     * Using WorkManager to guarantee work is done for offline mode
     */
    private fun updateUserFavoritesRemote(
        localUser: User,
        locallyFavorites: List<String>,
    ) {
        val syncFavoritesRequest = OneTimeWorkRequestBuilder<SyncFavoritesWorker>()
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS)
            .setConstraints(Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build())
            .setInputData(workDataOf(
                "FAVORITES_LIST" to locallyFavorites.toTypedArray(),
                "USER_ID" to localUser.userId,
            ))
            .build()

        workManager.enqueueUniqueWork(
            "favoritesSync",
            ExistingWorkPolicy.REPLACE,
            syncFavoritesRequest
        )
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