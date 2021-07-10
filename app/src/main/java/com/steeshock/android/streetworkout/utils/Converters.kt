package com.steeshock.android.streetworkout.utils

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.steeshock.android.streetworkout.data.model.Category
import java.util.*

class Converters {
    private var gson = Gson()

    @TypeConverter
    fun categoryListToString(someObjects: List<Int>?): String? {
        return gson.toJson(someObjects)
    }

    @TypeConverter
    fun fromString(data: String?): List<Int>? {

        if (data == null){
            return Collections.emptyList()
        }
        val listType = object : TypeToken<ArrayList<Int>>() {}.type
        return gson.fromJson(data, listType)
    }
}