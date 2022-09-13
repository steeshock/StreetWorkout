package com.steeshock.streetworkout.data.workers.common

import androidx.work.*
import com.steeshock.streetworkout.data.workers.SyncFavoritesWorker
import com.steeshock.streetworkout.data.workers.SyncFavoritesWorker.Companion.FAVORITES_DATA
import com.steeshock.streetworkout.data.workers.SyncFavoritesWorker.Companion.SYNC_FAVORITES_WORK
import com.steeshock.streetworkout.data.workers.SyncFavoritesWorker.Companion.USER_ID_DATA
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class WorkerService @Inject constructor(private val workManager: WorkManager) : IWorkerService {

    override fun syncFavorites(userId: String, locallyFavorites: List<String>) {
        val syncFavoritesRequest = OneTimeWorkRequestBuilder<SyncFavoritesWorker>()
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS)
            .setConstraints(Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build())
            .setInputData(workDataOf(
                FAVORITES_DATA to locallyFavorites.toTypedArray(),
                USER_ID_DATA to userId,
            ))
            .build()

        workManager.enqueueUniqueWork(
            SYNC_FAVORITES_WORK,
            ExistingWorkPolicy.REPLACE,
            syncFavoritesRequest
        )
    }

    override fun isUniqueWorkDone(uniqueWorkName: String): Boolean {
        return try {
            val workState = workManager.getWorkInfosForUniqueWork(uniqueWorkName).get().first().state
            workState == WorkInfo.State.SUCCEEDED || workState == WorkInfo.State.FAILED
        } catch (e: NoSuchElementException) {
            true
        }
    }
}