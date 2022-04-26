package com.steeshock.streetworkout.data.repository.implementation.firebase

import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.steeshock.streetworkout.common.Constants
import com.steeshock.streetworkout.data.database.UserInfoDao
import com.steeshock.streetworkout.data.model.UserInfo
import com.steeshock.streetworkout.data.repository.interfaces.IUserInfoRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class FirebaseUserInfoRepository(
    private val userInfoDao: UserInfoDao,
) : IUserInfoRepository {

    override suspend fun getOrCreateUserInfo(userId: String, name: String, email: String): UserInfo? {
        return getUserById(userId) ?: createUser(userId, name, email)
    }

    override suspend fun getUserFavorites(userId: String): List<String>? {
        return userInfoDao.getUserInfo(userId).favorites
    }

    private suspend fun createUser(userId: String, name: String, email: String): UserInfo? {
        return suspendCoroutine { continuation ->
            val database = Firebase.database(Constants.FIREBASE_PATH)
            val newUser = database.getReference("users").child(userId)
            val userInfo = UserInfo(userId, name, email)
            newUser.setValue(userInfo)
                .addOnSuccessListener {
                    CoroutineScope(Dispatchers.IO).launch {
                        userInfoDao.insertUserInfo(userInfo)
                    }
                    continuation.resume(userInfo)
                }
                .addOnFailureListener {
                    continuation.resumeWithException(Throwable("Failed to create new user"))
                }
        }
    }

    private suspend fun getUserById(userId: String): UserInfo? {
        return suspendCoroutine { continuation ->
            val database = Firebase.database(Constants.FIREBASE_PATH)
            val userById = database.getReference("users").child(userId)
            userById.get()
                .addOnSuccessListener { snapshot ->
                    val existentUser = snapshot.getValue<UserInfo>()
                    existentUser?.let {
                        CoroutineScope(Dispatchers.IO).launch {
                            userInfoDao.insertUserInfo(it)
                        }
                    }
                    continuation.resume(existentUser)
                }
                .addOnFailureListener {
                    continuation.resumeWithException(Throwable("Failed to get user information"))
                }
        }
    }
}