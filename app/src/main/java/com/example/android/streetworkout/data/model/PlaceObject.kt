package com.example.android.streetworkout.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "places_table")
data class PlaceObject(

    @ColumnInfo(name = "description")
    val description: String = "",

    @ColumnInfo(name = "imagePath")
    val imagePath: String = ""
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var Id: Int = (0..100).random()
}
