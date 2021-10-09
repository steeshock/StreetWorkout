package com.steeshock.android.streetworkout.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.steeshock.android.streetworkout.data.model.Category

@Dao
interface CategoriesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCategory(category: Category)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllCategories(category: List<Category>)

    @Query("SELECT * FROM ${Category.TABLE_NAME}")
    fun getCategoriesLive(): LiveData<List<Category>>

    @Query("DELETE FROM ${Category.TABLE_NAME}")
    fun clearCategoriesTable()

    @Update
    fun updateCategory(category: Category)
}