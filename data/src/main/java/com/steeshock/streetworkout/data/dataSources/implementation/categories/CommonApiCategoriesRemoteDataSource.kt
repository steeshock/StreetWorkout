package com.steeshock.streetworkout.data.dataSources.implementation.categories

import com.steeshock.streetworkout.data.api.PlacesAPI
import com.steeshock.streetworkout.data.dataSources.interfaces.remote.ICategoriesRemoteDataSource
import com.steeshock.streetworkout.data.model.dto.CategoryDto
import javax.inject.Inject

/**
 * Implementation for REST API
 */
class CommonApiCategoriesRemoteDataSource @Inject constructor(
    private val placesAPI: PlacesAPI,
    ) : ICategoriesRemoteDataSource {
    override suspend fun getCategoriesRemote(): List<CategoryDto> {
        return placesAPI.getCategories().body()!!
    }
}