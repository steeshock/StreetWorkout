package com.steeshock.streetworkout.interactor.repository

interface IDataStoreRepository {
    suspend fun putInt(preferencesKey: String, value: Int)
    suspend fun getInt(preferencesKey: String): Int?
}