package com.steeshock.streetworkout.data.dataSources.implementation.user

import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.steeshock.streetworkout.data.Constants
import com.steeshock.streetworkout.data.dataSources.interfaces.remote.IUserRemoteDataSource
import com.steeshock.streetworkout.data.model.dto.UserDto
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class FirebaseUserRemoteDataSource @Inject constructor() : IUserRemoteDataSource {

    companion object {
        private const val USER_REMOTE_STORAGE = "users"
    }

    override suspend fun getUserRemote(userId: String): UserDto? {
        return suspendCoroutine { continuation ->
            val database = Firebase.database(Constants.FIREBASE_PATH)
            val userByIdRef = database.getReference(USER_REMOTE_STORAGE).child(userId)
            userByIdRef.get()
                .addOnSuccessListener {
                    continuation.resume(it.getValue<UserDto>())
                }
                .addOnFailureListener {
                    continuation.resumeWithException(it)
                }
        }
    }

    override suspend fun createUserRemote(
        userId: String,
        displayName: String,
        email: String,
    ): UserDto {
        return suspendCoroutine { continuation ->
            val database = Firebase.database(Constants.FIREBASE_PATH)
            val newUserRef = database.getReference(USER_REMOTE_STORAGE).child(userId)
            val user = UserDto(userId, displayName, email)
            newUserRef.setValue(user)
                .addOnSuccessListener {
                    continuation.resume(user)
                }
                .addOnFailureListener {
                    continuation.resumeWithException(it)
                }
        }
    }
}