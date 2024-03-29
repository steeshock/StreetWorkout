package com.steeshock.streetworkout.data.dao

import androidx.room.*
import com.steeshock.streetworkout.data.model.dto.UserDto

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(userDto: UserDto)

    @Query("SELECT * FROM ${UserDto.TABLE_NAME} WHERE userId = :userId ")
    suspend fun getUserById(userId: String): UserDto?

    @Update
    suspend fun updateUser(userDto: UserDto)
}