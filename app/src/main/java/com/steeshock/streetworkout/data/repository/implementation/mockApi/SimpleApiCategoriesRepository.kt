package com.steeshock.streetworkout.data.repository.implementation.mockApi

import com.steeshock.streetworkout.data.api.PlacesAPI
import com.steeshock.streetworkout.data.database.CategoriesDao
import com.steeshock.streetworkout.data.mappers.mapToDto
import com.steeshock.streetworkout.data.mappers.mapToEntity
import com.steeshock.streetworkout.interactor.entity.Category
import com.steeshock.streetworkout.interactor.repository.ICategoriesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Repository for work with REST endpoints
 */
open class SimpleApiCategoriesRepository @Inject constructor(
    private val categoriesDao: CategoriesDao,
    private val placesAPI: PlacesAPI,
) : ICategoriesRepository {

    override val allCategories: Flow<List<Category>> = categoriesDao.getCategoriesFlow().map { categories ->
        categories.map { it.mapToEntity() }
    }

    override suspend fun fetchCategories(): Boolean {
        val result = placesAPI.getCategories()
        if (result.isSuccessful) {
            result.body()?.let { categoriesDao.insertAllCategories(it) }
            return true
        }
        return false
    }

    override suspend fun updateCategory(category: Category) {
        categoriesDao.updateCategory(category.mapToDto())
    }

    override suspend fun clearCategoriesTable() {
        categoriesDao.clearCategoriesTable()
    }
}
