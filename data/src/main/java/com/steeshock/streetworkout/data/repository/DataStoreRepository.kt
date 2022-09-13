package com.steeshock.streetworkout.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.steeshock.streetworkout.domain.repository.IDataStoreRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class DataStoreRepository @Inject constructor(
    private val context: Context,
) : IDataStoreRepository {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = USER_PREFERENCES_NAME)

    companion object {
        const val NIGHT_MODE_PREFERENCES_KEY = "night_mode"
        private const val USER_PREFERENCES_NAME = "user_preferences"
    }

    override suspend fun putInt(preferencesKey: String, value: Int) {
        context.dataStore.edit { preferences ->
            preferences[intPreferencesKey(preferencesKey)] = value
        }
    }

    override suspend fun getInt(preferencesKey: String): Int? {
        return try {
            val preferences = context.dataStore.data.first()
            preferences[intPreferencesKey(preferencesKey)]
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}