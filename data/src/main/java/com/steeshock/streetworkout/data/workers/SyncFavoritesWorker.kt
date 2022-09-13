package com.steeshock.streetworkout.data.workers

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.steeshock.streetworkout.data.Constants
import com.steeshock.streetworkout.data.model.dto.UserDto
import java.lang.Exception
import java.util.concurrent.CountDownLatch

class SyncFavoritesWorker(appContext: Context, private val workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {

    companion object {
        const val USER_ID_DATA = "USER_ID_DATA"
        const val FAVORITES_DATA = "FAVORITES_DATA"
        const val SYNC_FAVORITES_WORK = "SYNC_FAVORITES_WORK"
        const val MAX_RETRY_ATTEMPTS = 5

        class SyncFavoritesException : Exception("Sending offline favorites not completed")
    }

    override fun doWork(): Result {

        val latch = CountDownLatch(1)
        var workResult = Result.failure()

        val favorites = inputData.getStringArray(FAVORITES_DATA)!!.toList()
        val userId = inputData.getString(USER_ID_DATA)!!

        val database = Firebase.database(Constants.FIREBASE_PATH)
        val userByIdRef = database.getReference("users").child(userId)
        userByIdRef.get()
            .addOnSuccessListener { snapshot ->
                workResult = snapshot.getValue<UserDto>()?.let {
                    val updatedUser = it.copy(favorites = favorites)
                    userByIdRef.setValue(updatedUser)
                    Result.success()
                } ?: Result.failure()
                latch.countDown()
            }
            .addOnFailureListener {
                workResult = if (workerParams.runAttemptCount >= MAX_RETRY_ATTEMPTS) {
                    Result.failure()
                } else {
                    Result.retry()
                }
                latch.countDown()
            }
        latch.await()
        return workResult
    }
}

