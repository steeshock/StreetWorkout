package com.steeshock.streetworkout.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.steeshock.streetworkout.data.model.Place.Companion.TABLE_NAME

@Entity(tableName = TABLE_NAME)
data class Place(

    @PrimaryKey
    @ColumnInfo(name = "placeId")
    var placeId: String = "",

    @ColumnInfo(name = "userId")
    var userId: String = "",

    @ColumnInfo(name = "title")
    var title: String = "",

    @ColumnInfo(name = "description")
    var description: String = "",

    @ColumnInfo(name = "latitude")
    var latitude: Double = 54.513845,

    @ColumnInfo(name = "longitude")
    var longitude: Double = 36.261215,

    @ColumnInfo(name = "address")
    var address: String = "",

    @ColumnInfo(name = "created")
    val created: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "categories")
    var categories: ArrayList<Int>? = null,

    @ColumnInfo(name = "images")
    var images: List<String>? = null,

    @ColumnInfo(name = "isFavorite")
    var isFavorite: Boolean = false,

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

    companion object {
        const val TABLE_NAME = "places_table"
    }
}
