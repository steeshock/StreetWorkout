package com.steeshock.streetworkout.data.database

import androidx.room.*
import com.steeshock.streetworkout.data.model.User

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Query("SELECT * FROM ${User.TABLE_NAME} WHERE userId = :userId ")
    suspend fun getUserById(userId: String): User?

    @Update
    suspend fun updateUser(user: User)
}