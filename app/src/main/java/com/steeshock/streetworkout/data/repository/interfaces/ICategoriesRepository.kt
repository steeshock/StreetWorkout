package com.steeshock.streetworkout.data.repository.interfaces

import androidx.lifecycle.LiveData
import com.steeshock.streetworkout.data.model.Category

interface ICategoriesRepository {

    val allCategories: LiveData<List<Category>>

    /**
     * Fetch categories from remote source and and save data to local storage
     */
    suspend fun fetchCategories(): Boolean

    /**
     * Insert new place in local Places table
     */
    suspend fun insertCategoryLocal(newCategory: Category)

    /**
     * Insert list of categories in Categories table
     */
    suspend fun insertAllCategories(categories: List<Category>)

    /**
     * Update category in Category table
     */
    suspend fun updateCategory(category: Category)

    /**
     * Remove all data from Categories table
     */
    suspend fun clearCategoriesTable()
}