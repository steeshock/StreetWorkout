package com.steeshock.streetworkout.data.repository.dto

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = CategoryDto.TABLE_NAME)
data class CategoryDto(
    @PrimaryKey
    @ColumnInfo(name = "categoryId")
    var categoryId: Int? = null,

    @ColumnInfo(name = "categoryName")
    var categoryName: String = "",

    @ColumnInfo(name = "isSelected")
    var isSelected: Boolean? = false,
) {
    companion object {
        const val TABLE_NAME = "categories_table"
    }
}