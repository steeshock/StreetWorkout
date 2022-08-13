package com.steeshock.streetworkout.data.repository.implementation.mockApi

import androidx.lifecycle.LiveData
import com.steeshock.streetworkout.data.api.PlacesAPI
import com.steeshock.streetworkout.data.database.CategoriesDao
import com.steeshock.streetworkout.data.model.Category
import com.steeshock.streetworkout.data.repository.interfaces.ICategoriesRepository
import javax.inject.Inject

/**
 * Repository for work with REST endpoints
 */
open class SimpleApiCategoriesRepository @Inject constructor(
    private val categoriesDao: CategoriesDao,
    private val placesAPI: PlacesAPI,
) : ICategoriesRepository {

    override val allCategories: LiveData<List<Category>> = categoriesDao.getCategoriesLive()

    override suspend fun fetchCategories(): Boolean {
        val result = placesAPI.getCategories()
        if (result.isSuccessful) {
            result.body()?.let { insertAllCategories(it) }
            return true
        }
        return false
    }

    override suspend fun insertCategoryLocal(newCategory: Category) {
        categoriesDao.insertCategory(newCategory)
    }

    override suspend fun insertAllCategories(categories: List<Category>) {
        categoriesDao.insertAllCategories(categories)
    }

    override suspend fun updateCategory(category: Category) {
        categoriesDao.updateCategory(category)
    }

    override suspend fun clearCategoriesTable() {
        categoriesDao.clearCategoriesTable()
    }
}
