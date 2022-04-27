package com.steeshock.streetworkout.data.repository.implementation.mockApi

import androidx.lifecycle.LiveData
import com.steeshock.streetworkout.data.api.APIResponse
import com.steeshock.streetworkout.data.api.PlacesAPI
import com.steeshock.streetworkout.data.database.CategoriesDao
import com.steeshock.streetworkout.data.model.Category
import com.steeshock.streetworkout.data.repository.interfaces.ICategoriesRepository
import io.reactivex.rxjava3.disposables.CompositeDisposable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

/**
 * Repository for work with REST endpoints
 */
open class MockApiCategoriesRepository(
    private val categoriesDao: CategoriesDao,
    private val placesAPI: PlacesAPI,
) : ICategoriesRepository {

    override val allCategories: LiveData<List<Category>> = categoriesDao.getCategoriesLive()

    override suspend fun fetchCategories(onResponse: APIResponse<List<Category>>) {
        var result: Response<List<Category>>? = null
        try {
            result = placesAPI.getCategories()
        } catch (t: Throwable) {
            onResponse.onError(t)
        }
        withContext(Dispatchers.Main) {
            if (result?.isSuccessful == true) {
                onResponse.onSuccess(result.body())
            }
        }
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
