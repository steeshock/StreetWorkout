package com.steeshock.android.streetworkout.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = Category.TABLE_NAME)
data class Category(
    @PrimaryKey
    @ColumnInfo(name = "name")
    var name: String = "",

    @ColumnInfo(name = "isSelected")
    var isSelected: Boolean = false

) {

    fun changeSelectedState() {
        this.isSelected = !this.isSelected
    }

    companion object {
        const val TABLE_NAME = "categories_table"
    }
}