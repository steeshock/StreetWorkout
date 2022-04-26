package com.steeshock.streetworkout.data.model

data class User(

    val userId: String = "",

    val displayName: String = "",

    val email: String = "",

    val favorites: List<String>? = null,
)
