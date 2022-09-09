package com.steeshock.streetworkout.domain.entity

data class User(
    val userId: String = "",
    val displayName: String = "",
    val email: String = "",
    val favorites: List<String>? = null,
)
