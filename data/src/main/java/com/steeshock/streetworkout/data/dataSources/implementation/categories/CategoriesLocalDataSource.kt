package com.steeshock.streetworkout.data.dataSources.implementation.categories

import com.steeshock.streetworkout.data.dataSources.interfaces.local.ICategoriesLocalDataSource
import com.steeshock.streetworkout.data.dao.CategoriesDao
import com.steeshock.streetworkout.data.mappers.mapToEntity
import com.steeshock.streetworkout.data.repository.dto.CategoryDto
import com.steeshock.streetworkout.domain.entity.Category
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CategoriesLocalDataSource @Inject constructor(
    private val categoriesDao: CategoriesDao,
) : ICategoriesLocalDataSource {

    override val allCategories: Flow<List<Category>> = categoriesDao.getCategoriesFlow().map { categories ->
        categories.map { it.mapToEntity() }
    }

    override suspend fun getCategoryById(categoryId: Int?): CategoryDto? {
        return categoriesDao.getCategoryById(categoryId)
    }

    override suspend fun insertAllCategoriesLocal(categories: List<CategoryDto>) {
        categoriesDao.insertAllCategories(categories)
    }

    override suspend fun updateCategoryLocal(categoryDto: CategoryDto) {
        categoriesDao.updateCategory(categoryDto)
    }

    override suspend fun clearCategoriesTable() {
        categoriesDao.clearCategoriesTable()
    }
}