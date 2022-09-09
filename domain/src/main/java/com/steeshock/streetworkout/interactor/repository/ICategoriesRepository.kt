package com.steeshock.streetworkout.interactor.repository

import com.steeshock.streetworkout.interactor.entity.Category
import kotlinx.coroutines.flow.Flow

interface ICategoriesRepository {

    val allCategories: Flow<List<Category>>

    /**
     * Fetch categories from remote source and and save data to local storage
     */
    suspend fun fetchCategories(): Boolean

    /**
     * Update category in Category table
     */
    suspend fun updateCategory(category: Category)

    /**
     * Remove all data from Categories table
     */
    suspend fun clearCategoriesTable()
}