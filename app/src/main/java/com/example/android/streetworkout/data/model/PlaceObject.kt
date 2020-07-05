package com.example.android.streetworkout.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "places_table")
data class PlaceObject(

    @ColumnInfo(name = "imagePath")
    val imagePath: String = "https://picsum.photos/30${(0..9).random()}/200",

    @ColumnInfo(name = "title")
    val title: String = "Случайное место",

    @ColumnInfo(name = "description")
    val description: String = "Улица Пушкина, дом Колотушкина Квартира Петрова, спросить Вольнова",

    @ColumnInfo(name = "latitude")
    val latitude: Double = 54.513845,

    @ColumnInfo(name = "longitude")
    val longitude: Double = 36.261215,

    @ColumnInfo(name = "address")
    val address: String = "Неизвестный адрес",

    @ColumnInfo(name = "isFavorite")
    val isFavorite: Boolean = false

) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = (0..100).random()
}
