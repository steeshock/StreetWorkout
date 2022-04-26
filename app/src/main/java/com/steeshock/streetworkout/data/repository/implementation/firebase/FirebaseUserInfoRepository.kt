package com.steeshock.streetworkout.data.repository.implementation.firebase

import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.steeshock.streetworkout.common.Constants
import com.steeshock.streetworkout.data.model.User
import com.steeshock.streetworkout.data.repository.interfaces.IUserInfoRepository
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class FirebaseUserInfoRepository : IUserInfoRepository {

    override suspend fun getOrCreateUserInfo(userId: String, name: String, email: String): User? {
        return getUserById(userId) ?: createUser(userId, name, email)
    }

    private suspend fun createUser(userId: String, name: String, email: String): User? {
        return suspendCoroutine { continuation ->
            val database = Firebase.database(Constants.FIREBASE_PATH)
            val newUser = database.getReference("users").child(userId)
            val user = User(userId, name, email)
            newUser.setValue(user)
                .addOnSuccessListener {
                    // TODO Сохранить инфо о юзере в локальную БД
                    continuation.resume(user)
                }
                .addOnFailureListener {
                    continuation.resumeWithException(Throwable("Failed to create new user"))
                }
        }
    }

    private suspend fun getUserById(userId: String): User? {
        return suspendCoroutine { continuation ->
            val database = Firebase.database(Constants.FIREBASE_PATH)
            val userById = database.getReference("users").child(userId)
            userById.get()
                .addOnSuccessListener {
                    // TODO Сохранить инфо о юзере в локальную БД
                    continuation.resume(it.getValue<User>())
                }
                .addOnFailureListener {
                    continuation.resumeWithException(Throwable("Failed to get user information"))
                }
        }
    }
}