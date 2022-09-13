package com.steeshock.streetworkout.data.repository

import com.steeshock.streetworkout.data.dataSources.interfaces.local.ICategoriesLocalDataSource
import com.steeshock.streetworkout.data.dataSources.interfaces.remote.ICategoriesRemoteDataSource
import com.steeshock.streetworkout.data.mappers.mapToDto
import com.steeshock.streetworkout.domain.entity.Category
import com.steeshock.streetworkout.domain.repository.ICategoriesRepository
import javax.inject.Inject

class CategoriesRepository @Inject constructor(
    private val categoriesRemoteDataSource: ICategoriesRemoteDataSource,
    private val categoriesLocalDataSource: ICategoriesLocalDataSource,
) : ICategoriesRepository {

    override val allCategories = categoriesLocalDataSource.allCategories

    override suspend fun fetchCategories() {
        val remoteCategories = categoriesRemoteDataSource.getCategoriesRemote()
        remoteCategories.forEach {
            val isSelected = categoriesLocalDataSource.getCategoryById(it.categoryId)?.isSelected
            it.isSelected = isSelected
        }
        categoriesLocalDataSource.insertAllCategoriesLocal(remoteCategories)
    }

    override suspend fun updateCategory(category: Category) {
        categoriesLocalDataSource.updateCategoryLocal(category.mapToDto())
    }

    override suspend fun clearCategoriesTable() {
        categoriesLocalDataSource.clearCategoriesTable()
    }
}