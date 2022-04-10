package com.steeshock.streetworkout.data.repository.interfaces

import androidx.datastore.preferences.core.Preferences

interface IDataStoreRepository {
    suspend fun putInt(preferencesKey: Preferences.Key<Int>, value: Int)
    suspend fun getInt(preferencesKey: Preferences.Key<Int>): Int?
}