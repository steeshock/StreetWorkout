package com.steeshock.streetworkout.data.dataSources.interfaces.remote

import com.steeshock.streetworkout.data.repository.dto.PlaceDto

interface IPlacesRemoteDataSource {
    /**
     * Get list of all places from remote Places storage
     */
    suspend fun getPlacesRemote(): List<PlaceDto>

    /**
     * Upload image with file [uri]
     * @return String uri with uploaded file
     */
    suspend fun uploadImage(uri: String, placeId: String?): String?

    /**
     * Insert new place in Remote Places storage
     */
    suspend fun insertPlaceRemote(placeDto: PlaceDto)

    /**
     * Delete place in remote storage
     */
    suspend fun deletePlaceRemote(placeDto: PlaceDto): Boolean
}