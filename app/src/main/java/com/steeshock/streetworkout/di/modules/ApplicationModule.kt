package com.steeshock.streetworkout.di.modules

import android.content.Context
import androidx.work.WorkManager
import com.steeshock.streetworkout.data.workers.common.IWorkerService
import com.steeshock.streetworkout.data.workers.common.WorkerService
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ApplicationModule(private val appContext: Context) {

    @Provides
    @Singleton
    fun provideWorkerService(workManager: WorkManager): IWorkerService {
        return WorkerService(workManager)
    }

    @Provides
    @Singleton
    fun provideWorkManager(appContext: Context): WorkManager {
        return WorkManager.getInstance(appContext)
    }

    @Provides
    @Singleton
    fun provideAppContext(): Context {
        return appContext
    }
}