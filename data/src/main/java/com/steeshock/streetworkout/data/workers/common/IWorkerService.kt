package com.steeshock.streetworkout.data.workers.common

/**
 * Describes jobs (works) performs with WorkManager
 * and some helper utils for WorkManager
 */
interface IWorkerService {
    /**
     * Sync favorites list with remote (offline mode)
     */
    fun syncFavorites(userId: String, locallyFavorites: List<String>)

    /**
     * Check if unique work is succeeded of failed
     */
    fun isUniqueWorkDone(uniqueWorkName: String): Boolean
}
