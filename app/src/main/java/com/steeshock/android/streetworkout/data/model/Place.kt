package com.steeshock.android.streetworkout.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.steeshock.android.streetworkout.data.model.Place.Companion.TABLE_NAME
import com.steeshock.android.streetworkout.utils.Converters
import java.io.Serializable

@Entity(tableName = TABLE_NAME)
data class Place(

    @PrimaryKey
    @ColumnInfo(name = "place_id")
    var place_id: Int? = null,

    @ColumnInfo(name = "imagePath")
    var imagePath: String = "https://picsum.photos/30${(0..9).random()}/200",

    @ColumnInfo(name = "title")
    var title: String,

    @ColumnInfo(name = "description")
    var description: String,

    @ColumnInfo(name = "latitude")
    var latitude: Double,

    @ColumnInfo(name = "longitude")
    var longitude: Double,

    @ColumnInfo(name = "address")
    var address: String,

    @ColumnInfo(name = "created")
    val created: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "categories")
    var categories: ArrayList<Int>?

) {
    @ColumnInfo(name = "isFavorite")
    var isFavorite: Boolean? = null

    init {
        if (imagePath.isEmpty()) imagePath = "https://picsum.photos/30${(0..9).random()}/200"
        if (title.isEmpty()) title = "Случайное место"
        if (description.isEmpty()) description =
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua"
        if (latitude == 0.0) latitude = 54.513845
        if (longitude == 0.0) longitude = 36.261215
        if (address.isEmpty()) address =
            "Улица Пушкина, дом Колотушкина Квартира Петрова, спросить Вольнова"
        if (categories.isNullOrEmpty()) categories = arrayListOf()
    }

    fun changeFavoriteState() {
        if (this.isFavorite == null){
            this.isFavorite = true;
        } else {
            this.isFavorite = !this.isFavorite!!
        }
    }

    companion object {
        const val TABLE_NAME = "places_table"
    }
}
