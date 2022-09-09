package com.steeshock.streetworkout.data.mappers

import com.steeshock.streetworkout.data.repository.dto.UserDto
import com.steeshock.streetworkout.interactor.entity.User

fun UserDto.mapToEntity(): User {
    return User(
        userId = this.userId,
        displayName = this.displayName,
        email = this.email,
        favorites = this.favorites,
    )
}