package com.steeshock.streetworkout.data.repository.implementation.firebase

import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.steeshock.streetworkout.common.Constants.FIREBASE_PATH
import com.steeshock.streetworkout.data.database.CategoriesDao
import com.steeshock.streetworkout.data.mappers.mapToDto
import com.steeshock.streetworkout.data.mappers.mapToEntity
import com.steeshock.streetworkout.data.model.CategoryDto
import com.steeshock.streetworkout.domain.entity.Category
import com.steeshock.streetworkout.domain.repository.ICategoriesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * Repository for work with Firebase Realtime Database
 */
open class FirebaseCategoriesRepository @Inject constructor(
    private val categoriesDao: CategoriesDao,
) : ICategoriesRepository {

    override val allCategories: Flow<List<Category>> = categoriesDao.getCategoriesFlow().map { categories ->
        categories.map { it.mapToEntity() }
    }

    override suspend fun fetchCategories(): Boolean {
        return suspendCoroutine { continuation ->
            val database = Firebase.database(FIREBASE_PATH)
            val categoryDtos: MutableList<CategoryDto> = mutableListOf()

            database.getReference("categories").get().addOnSuccessListener {
                CoroutineScope(Dispatchers.IO).launch {
                    for (child in it.children) {
                        val category = child.getValue<CategoryDto>()
                        val isSelected = categoriesDao.getCategoryById(categoryId = category?.categoryId)?.isSelected
                        category?.isSelected = isSelected
                        category?.let { c -> categoryDtos.add(c) }
                    }
                    categoriesDao.insertAllCategories(categoryDtos)
                    continuation.resume(true)
                }
            }.addOnFailureListener {
                continuation.resumeWithException(it)
            }
        }
    }

    override suspend fun updateCategory(category: Category) {
        categoriesDao.updateCategory(category.mapToDto())
    }

    override suspend fun clearCategoriesTable() {
        categoriesDao.clearCategoriesTable()
    }
}