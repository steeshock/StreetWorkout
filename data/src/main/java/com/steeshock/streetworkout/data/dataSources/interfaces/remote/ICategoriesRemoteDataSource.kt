package com.steeshock.streetworkout.data.dataSources.interfaces.remote

import com.steeshock.streetworkout.data.model.dto.CategoryDto

interface ICategoriesRemoteDataSource {
    /**
     * Get list of all categories from remote Places storage
     */
    suspend fun getCategoriesRemote(): List<CategoryDto>
}