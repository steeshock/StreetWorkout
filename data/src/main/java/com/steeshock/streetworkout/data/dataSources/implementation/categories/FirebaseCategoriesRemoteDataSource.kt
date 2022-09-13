package com.steeshock.streetworkout.data.dataSources.implementation.categories

import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.steeshock.streetworkout.data.Constants
import com.steeshock.streetworkout.data.dataSources.interfaces.remote.ICategoriesRemoteDataSource
import com.steeshock.streetworkout.data.model.dto.CategoryDto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * Implementation for Firebase Realtime Database
 */
class FirebaseCategoriesRemoteDataSource @Inject constructor() : ICategoriesRemoteDataSource {

    companion object {
        private const val CATEGORIES_REMOTE_STORAGE = "categories"
    }

    override suspend fun getCategoriesRemote(): List<CategoryDto> {
        return suspendCoroutine { continuation ->
            val database = Firebase.database(Constants.FIREBASE_PATH)
            database.getReference(CATEGORIES_REMOTE_STORAGE).get().addOnSuccessListener {
                CoroutineScope(Dispatchers.IO).launch {
                    val categories = it.children.mapNotNull { child-> child.getValue<CategoryDto>()  }
                    continuation.resume(categories)
                }
            }.addOnFailureListener {
                continuation.resumeWithException(it)
            }
        }
    }
}