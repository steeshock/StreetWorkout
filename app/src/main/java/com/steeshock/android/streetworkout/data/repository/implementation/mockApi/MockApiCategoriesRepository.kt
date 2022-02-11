package com.steeshock.android.streetworkout.data.repository.implementation.mockApi

import androidx.lifecycle.LiveData
import com.steeshock.android.streetworkout.data.api.APIResponse
import com.steeshock.android.streetworkout.data.api.PlacesAPI
import com.steeshock.android.streetworkout.data.database.CategoriesDao
import com.steeshock.android.streetworkout.data.model.Category
import com.steeshock.android.streetworkout.data.repository.interfaces.ICategoriesRepository
import io.reactivex.rxjava3.disposables.CompositeDisposable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository for work with REST endpoints
 */
open class MockApiCategoriesRepository(
    private val categoriesDao: CategoriesDao,
    private val placesAPI: PlacesAPI,
) : ICategoriesRepository {

    override val allCategories: LiveData<List<Category>> = categoriesDao.getCategoriesLive()

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    companion object {

        @Volatile
        private var instance: MockApiCategoriesRepository? = null

        fun getInstance(
            categoriesDao: CategoriesDao,
            placesAPI: PlacesAPI
        ) = instance
                ?: synchronized(this) {
                    instance
                        ?: MockApiCategoriesRepository(
                            categoriesDao,
                            placesAPI
                        )
                            .also { instance = it }
                }
    }

    override suspend fun fetchCategories(onResponse: APIResponse<List<Category>>) {
        val response = placesAPI.getCategories()
        withContext(Dispatchers.Main) {
            if (response.isSuccessful) {
                onResponse.onSuccess(response.body())
            }
        }
        /**
         * Obsolete RxJava approach
         */
        /*
        placesAPI.getCategories()
            .subscribeOn(io.reactivex.rxjava3.schedulers.Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                onResponse.onSuccess(it)
            }, {
                onResponse.onError(it)
            }).also {
                compositeDisposable.add(it)
            }
         */
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