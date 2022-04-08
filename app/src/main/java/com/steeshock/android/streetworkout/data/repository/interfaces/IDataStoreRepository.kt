package com.steeshock.android.streetworkout.data.repository.interfaces

interface IDataStoreRepository {
    suspend fun putInt(key: String, value: Int)
    suspend fun getInt(key: String): Int?
}