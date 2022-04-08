package com.steeshock.android.streetworkout.data.repository.implementation

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.steeshock.android.streetworkout.data.repository.interfaces.IDataStoreRepository
import kotlinx.coroutines.flow.first

private const val USER_PREFERENCES_NAME = "user_preferences"

class DataStoreRepository(
    private val context: Context,
) : IDataStoreRepository {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = USER_PREFERENCES_NAME)

    companion object PreferencesKeys {
        val NIGHT_MODE_PREFERENCES_KEY = intPreferencesKey("night_mode")
    }

    override suspend fun putInt(preferencesKey: Preferences.Key<Int>, value: Int) {
        context.dataStore.edit { preferences ->
            preferences[preferencesKey] = value
        }
    }

    override suspend fun getInt(preferencesKey: Preferences.Key<Int>): Int? {
        return try {
            val preferences = context.dataStore.data.first()
            preferences[preferencesKey]
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}