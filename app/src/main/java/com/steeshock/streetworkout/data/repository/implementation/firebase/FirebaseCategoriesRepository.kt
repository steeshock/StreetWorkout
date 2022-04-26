package com.steeshock.streetworkout.data.repository.implementation.firebase

import androidx.lifecycle.LiveData
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.steeshock.streetworkout.common.Constants.FIREBASE_PATH
import com.steeshock.streetworkout.data.api.APIResponse
import com.steeshock.streetworkout.data.database.CategoriesDao
import com.steeshock.streetworkout.data.model.Category
import com.steeshock.streetworkout.data.repository.interfaces.ICategoriesRepository

/**
 * Repository for work with Firebase Realtime Database
 */
open class FirebaseCategoriesRepository(
    private val categoriesDao: CategoriesDao
) : ICategoriesRepository {

    override val allCategories: LiveData<List<Category>> = categoriesDao.getCategoriesLive()

    companion object {

        @Volatile
        private var instance: FirebaseCategoriesRepository? = null

        /**
         * Singleton instance creator without Dagger scope annotations
         */
        fun getInstance(categoriesDao: CategoriesDao) =
            instance
                ?: synchronized(this) {
                    instance ?: FirebaseCategoriesRepository(categoriesDao).also { instance = it }
                }
    }

    override suspend fun fetchCategories(onResponse: APIResponse<List<Category>>) {
        val database = Firebase.database(FIREBASE_PATH)
        val categories: MutableList<Category> = mutableListOf()

        database.getReference("categories").get().addOnSuccessListener {

            for (child in it.children) {

                val category = child.getValue<Category>()

                val isSelected =
                    allCategories.value?.find { p -> p.category_id == category?.category_id }?.isSelected

                category?.isSelected = isSelected

                category?.let { c -> categories.add(c) }
            }

            onResponse.onSuccess(categories)

        }.addOnFailureListener {
            onResponse.onError(it)
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