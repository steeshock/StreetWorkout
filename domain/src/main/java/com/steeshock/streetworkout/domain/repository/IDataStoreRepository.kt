package com.steeshock.streetworkout.domain.repository

interface IDataStoreRepository {
    suspend fun putInt(preferencesKey: String, value: Int)
    suspend fun getInt(preferencesKey: String): Int?
}