package com.steeshock.streetworkout.di.modules

import android.content.Context
import androidx.work.WorkManager
import com.steeshock.streetworkout.data.workers.common.IWorkerService
import com.steeshock.streetworkout.data.workers.common.WorkerService
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
interface WorkerModule {
    @Binds
    @Singleton
    fun bindWorkerService(workerService: WorkerService): IWorkerService

    companion object {
        @Provides
        @Singleton
        fun provideWorkManager(appContext: Context): WorkManager {
            return WorkManager.getInstance(appContext)
        }
    }
}