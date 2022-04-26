package com.steeshock.streetworkout.data.database

import androidx.room.*
import com.steeshock.streetworkout.data.model.UserInfo

@Dao
interface UserInfoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserInfo(userInfo: UserInfo)

    @Update
    suspend fun updateUserInfo(userInfo: UserInfo)

    @Query("SELECT * FROM ${UserInfo.TABLE_NAME} WHERE userId = :userId ")
    suspend fun getUserInfo(userId: String): UserInfo
}