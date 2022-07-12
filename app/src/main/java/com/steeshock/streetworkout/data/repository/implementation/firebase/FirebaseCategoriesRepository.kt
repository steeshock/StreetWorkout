package com.steeshock.streetworkout.data.repository.implementation.firebase

import androidx.lifecycle.LiveData
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.steeshock.streetworkout.common.Constants.FIREBASE_PATH
import com.steeshock.streetworkout.data.database.CategoriesDao
import com.steeshock.streetworkout.data.model.Category
import com.steeshock.streetworkout.data.repository.interfaces.ICategoriesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * Repository for work with Firebase Realtime Database
 */
open class FirebaseCategoriesRepository @Inject constructor(
    private val categoriesDao: CategoriesDao
) : ICategoriesRepository {

    override val allCategories: LiveData<List<Category>> = categoriesDao.getCategoriesLive()

    override suspend fun fetchCategories(): Boolean {
        return suspendCoroutine { continuation ->
            val database = Firebase.database(FIREBASE_PATH)
            val categories: MutableList<Category> = mutableListOf()

            database.getReference("categories").get().addOnSuccessListener {

                for (child in it.children) {
                    val category = child.getValue<Category>()
                    val isSelected = allCategories.value?.find { p -> p.category_id == category?.category_id }?.isSelected
                    category?.isSelected = isSelected
                    category?.let { c -> categories.add(c) }
                }

                CoroutineScope(Dispatchers.IO).launch {
                    categoriesDao.insertAllCategories(categories)
                    continuation.resume(true)
                }

            }.addOnFailureListener {
                continuation.resumeWithException(it)
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