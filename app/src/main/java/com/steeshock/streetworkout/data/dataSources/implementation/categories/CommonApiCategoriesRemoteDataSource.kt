package com.steeshock.streetworkout.data.dataSources.implementation.categories

import com.steeshock.streetworkout.data.api.PlacesAPI
import com.steeshock.streetworkout.data.dataSources.remote.ICategoriesRemoteDataSource
import com.steeshock.streetworkout.data.dataSources.remote.IPlacesRemoteDataSource
import com.steeshock.streetworkout.data.repository.dto.CategoryDto
import com.steeshock.streetworkout.data.repository.dto.PlaceDto
import com.steeshock.streetworkout.domain.entity.Place
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