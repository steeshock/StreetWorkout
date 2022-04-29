package com.steeshock.streetworkout.data.repository.implementation.firebase

import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.steeshock.streetworkout.common.Constants
import com.steeshock.streetworkout.data.database.UserDao
import com.steeshock.streetworkout.data.model.User
import com.steeshock.streetworkout.data.repository.interfaces.IUserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class FirebaseUserRepository(
    private val userDao: UserDao,
) : IUserRepository {

    override suspend fun getOrCreateUser(userId: String, name: String, email: String): User? {
        return fetchUser(userId) ?: createUser(userId, name, email)
    }

    override suspend fun getUserFavorites(userId: String): List<String>? {
        return userDao.getUserById(userId).favorites
    }

    private suspend fun createUser(userId: String, name: String, email: String): User? {
        return suspendCoroutine { continuation ->
            val database = Firebase.database(Constants.FIREBASE_PATH)
            val newUserRef = database.getReference("users").child(userId)
            val user = User(userId, name, email)
            newUserRef.setValue(user)
                .addOnSuccessListener {
                    CoroutineScope(Dispatchers.IO).launch {
                        userDao.insertUser(user)
                    }
                    continuation.resume(user)
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
                    val existentUser = snapshot.getValue<User>()
                    existentUser?.let {
                        CoroutineScope(Dispatchers.IO).launch {
                            userDao.insertUser(it)
                        }
                    }
                    continuation.resume(existentUser)
                }
                .addOnFailureListener {
                    continuation.resumeWithException(it)
                }
        }
    }
}