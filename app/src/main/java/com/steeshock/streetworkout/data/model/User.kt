package com.steeshock.streetworkout.data.model

data class User(

    val displayName: String? = "",

    val email: String? = "",

    val favorites: List<Int>? = null,
)
