package com.steeshock.streetworkout.data.dataSources.implementation.places

import com.steeshock.streetworkout.data.api.PlacesAPI
import com.steeshock.streetworkout.data.dataSources.interfaces.remote.IPlacesRemoteDataSource
import com.steeshock.streetworkout.data.repository.dto.PlaceDto
import com.steeshock.streetworkout.domain.entity.Place
import javax.inject.Inject

/**
 * Implementation for REST API
 */
class CommonApiPlacesRemoteDataSource @Inject constructor(
    private val placesAPI: PlacesAPI,
    ) : IPlacesRemoteDataSource {

    override suspend fun getPlacesRemote(): List<PlaceDto> {
        return placesAPI.getPlaces().body()!!
    }

    override suspend fun uploadImage(uri: String, placeId: String?): String? {
        TODO("Not yet implemented")
    }

    override suspend fun insertPlaceRemote(newPlace: Place) {
        TODO("Not yet implemented")
    }

    override suspend fun deletePlaceRemote(place: Place): Boolean {
        TODO("Not yet implemented")
    }
}