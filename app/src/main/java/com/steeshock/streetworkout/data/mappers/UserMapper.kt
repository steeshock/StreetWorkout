package com.steeshock.streetworkout.data.mappers

import com.steeshock.streetworkout.data.model.UserDto
import com.steeshock.streetworkout.domain.entity.User

fun UserDto.mapToEntity(): User {
    return User(
        userId = this.userId,
        displayName = this.displayName,
        email = this.email,
        favorites = this.favorites,
    )
}