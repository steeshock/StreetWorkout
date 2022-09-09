package com.steeshock.streetworkout.interactor.entity

data class User(
    val userId: String = "",
    val displayName: String = "",
    val email: String = "",
    val favorites: List<String>? = null,
)
