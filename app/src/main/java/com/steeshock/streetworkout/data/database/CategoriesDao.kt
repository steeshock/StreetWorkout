package com.steeshock.streetworkout.data.database

import androidx.room.*
import com.steeshock.streetworkout.data.repository.dto.CategoryDto
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoriesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCategory(categoryDto: CategoryDto)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllCategories(categoryDto: List<CategoryDto>)

    @Query("SELECT * FROM ${CategoryDto.TABLE_NAME}")
    fun getCategoriesFlow(): Flow<List<CategoryDto>>

    @Query("SELECT * FROM ${CategoryDto.TABLE_NAME} WHERE categoryId = :categoryId")
    fun getCategoryById(categoryId: Int?): CategoryDto?

    @Query("DELETE FROM ${CategoryDto.TABLE_NAME}")
    fun clearCategoriesTable()

    @Update
    fun updateCategory(categoryDto: CategoryDto)
}