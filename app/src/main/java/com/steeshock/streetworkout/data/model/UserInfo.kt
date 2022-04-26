package com.steeshock.streetworkout.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = UserInfo.TABLE_NAME)
data class UserInfo(

    @PrimaryKey
    @ColumnInfo(name = "userId")
    val userId: String = "",

    @ColumnInfo(name = "displayName")
    val displayName: String = "",

    @ColumnInfo(name = "email")
    val email: String = "",

    @ColumnInfo(name = "favorites")
    val favorites: ArrayList<String>? = null,
) {
    companion object {
        const val TABLE_NAME = "user_info_table"
    }
}
