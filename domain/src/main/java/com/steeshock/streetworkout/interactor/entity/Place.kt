package com.steeshock.streetworkout.interactor.entity

data class Place(
    var placeId: String = "",
    var userId: String = "",
    var title: String = "",
    var description: String = "",
    var latitude: Double = 54.513845,
    var longitude: Double = 36.261215,
    var address: String = "",
    val created: Long = System.currentTimeMillis(),
    var categories: ArrayList<Int>? = null,
    var images: List<String>? = null,

    /**
     * true if place added to favorites list
     */
    var isFavorite: Boolean = false,

    /**
     * true if current authorized user created this place
     */
    var isUserPlaceOwner: Boolean = false,

    ) {
    init {
        if (title.isEmpty()) title = "Случайное место"
        if (description.isEmpty()) description =
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua"
        if (latitude == 0.0) latitude = 54.513845
        if (longitude == 0.0) longitude = 36.261215
        if (address.isEmpty()) address =
            "Улица Пушкина, дом Колотушкина Квартира Петрова, спросить Вольнова"
        if (categories.isNullOrEmpty()) categories = arrayListOf()
        if (images.isNullOrEmpty()) images = arrayListOf()
    }
}
