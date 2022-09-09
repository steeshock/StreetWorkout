package com.steeshock.streetworkout.data.mappers

import com.steeshock.streetworkout.data.model.CategoryDto
import com.steeshock.streetworkout.data.model.PlaceDto
import com.steeshock.streetworkout.data.model.UserDto
import com.steeshock.streetworkout.domain.entity.Category
import com.steeshock.streetworkout.domain.entity.Place
import com.steeshock.streetworkout.domain.entity.User

fun PlaceDto.mapToEntity(): Place {
    return Place(
        placeId = this.placeId,
        userId = this.userId,
        title = this.title,
        description = this.description,
        latitude = this.latitude,
        longitude = this.longitude,
        address = this.address,
        created = this.created,
        categories = this.categories,
        images = this.images,
        isFavorite = this.isFavorite,
        isUserPlaceOwner = this.isUserPlaceOwner,
    )
}
fun Place.mapToDto(): PlaceDto {
    return PlaceDto(
        placeId = this.placeId,
        userId = this.userId,
        title = this.title,
        description = this.description,
        latitude = this.latitude,
        longitude = this.longitude,
        address = this.address,
        created = this.created,
        categories = this.categories,
        images = this.images,
        isFavorite = this.isFavorite,
        isUserPlaceOwner = this.isUserPlaceOwner,
    )
}