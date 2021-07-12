package com.steeshock.android.streetworkout.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = Category.TABLE_NAME)
data class Category(

    @PrimaryKey
    @ColumnInfo(name = "category_id")
    var category_id: Int? = null,

    @ColumnInfo(name = "name")
    var category_name: String = "",

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