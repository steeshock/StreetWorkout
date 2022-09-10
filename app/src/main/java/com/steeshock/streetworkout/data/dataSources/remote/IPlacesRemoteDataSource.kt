package com.steeshock.streetworkout.data.dataSources.remote

import com.steeshock.streetworkout.data.repository.dto.PlaceDto
import com.steeshock.streetworkout.interactor.entity.Place

interface IPlacesRemoteDataSource {
    /**
     * Get list of all places from remote Places storage
     */
    suspend fun getPlacesRemote(): List<PlaceDto>

    /**
     * Upload image with file [uri]
     * @return Uri with uploaded file
     */
    suspend fun uploadImage(uri: String, placeId: String?): String?

    /**
     * Insert new place in Remote Places storage
     */
    suspend fun insertPlaceRemote(newPlace: Place)

    /**
     * Delete place in remote storage
     */
    suspend fun deletePlaceRemote(place: Place): Boolean
}