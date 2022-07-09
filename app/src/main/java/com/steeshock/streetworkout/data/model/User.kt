package com.steeshock.streetworkout.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = User.TABLE_NAME)
data class User(

    @PrimaryKey
    @ColumnInfo(name = "userId")
    val userId: String = "",

    @ColumnInfo(name = "displayName")
    val displayName: String = "",

    @ColumnInfo(name = "email")
    val email: String = "",

    @ColumnInfo(name = "favorites")
    val favorites: List<String>? = null,
) {
    companion object {
        const val TABLE_NAME = "user_table"
    }
}
