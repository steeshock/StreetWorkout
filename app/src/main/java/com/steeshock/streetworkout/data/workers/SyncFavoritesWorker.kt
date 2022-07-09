package com.steeshock.streetworkout.data.workers

import android.content.Context
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.steeshock.streetworkout.common.Constants
import com.steeshock.streetworkout.data.model.User
import java.util.concurrent.CountDownLatch

class SyncFavoritesWorker(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {
    override fun doWork(): Result {

        val latch = CountDownLatch(1)
        var workResult = Result.failure()

        val favorites = inputData.getStringArray("FAVORITES_LIST")!!.toList()
        val userId = inputData.getString("USER_ID")!!

        val database = Firebase.database(Constants.FIREBASE_PATH)
        val userByIdRef = database.getReference("users").child(userId)
        userByIdRef.get()
            .addOnSuccessListener { snapshot ->
                val existentUser = snapshot.getValue<User>()
                val updatedUser = existentUser?.copy(favorites = favorites)
                userByIdRef.setValue(updatedUser)
                workResult = Result.success()
                latch.countDown()
            }
            .addOnFailureListener {
                workResult = Result.retry()
                latch.countDown()
            }

        latch.await()

        return workResult
    }
}