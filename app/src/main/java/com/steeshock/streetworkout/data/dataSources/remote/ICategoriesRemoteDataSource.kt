package com.steeshock.streetworkout.data.dataSources.remote

import com.steeshock.streetworkout.data.repository.dto.CategoryDto

interface ICategoriesRemoteDataSource {
    /**
     * Get list of all categories from remote Places storage
     */
    suspend fun getCategoriesRemote(): List<CategoryDto>
}