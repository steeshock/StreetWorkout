package com.steeshock.streetworkout.data.dataSources.local

import com.steeshock.streetworkout.data.repository.dto.CategoryDto
import com.steeshock.streetworkout.domain.entity.Category
import kotlinx.coroutines.flow.Flow

interface ICategoriesLocalDataSource {

    val allCategories: Flow<List<Category>>

    /**
     * Returns category by id from local storage
     */
    suspend fun getCategoryById(categoryId: Int?): CategoryDto?

    /**
     * Insert all categories in Local Categories storage
     */
    suspend fun insertAllCategoriesLocal(categories: List<CategoryDto>)

    /**
     * Update category in Category table locally
     */
    suspend fun updateCategoryLocal(categoryDto: CategoryDto)

    /**
     * Remove all data from Categories table
     * TODO Temporary function for easy testing
     */
    suspend fun clearCategoriesTable()
}