package com.steeshock.android.streetworkout.utils

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.steeshock.android.streetworkout.data.model.Category
import java.util.*
import kotlin.collections.ArrayList

class Converters {
    private var gson = Gson()

    @TypeConverter
    fun categoryListToString(someObjects: ArrayList<Int>?): String? {
        var j = gson.toJson(someObjects)
        return gson.toJson(someObjects)
    }

    @TypeConverter
    fun fromString(data: String?): ArrayList<Int>? {

        if (data == null){
            return arrayListOf()
        }
        val listType = object : TypeToken<ArrayList<Int>>() {}.type
        return gson.fromJson(data, listType)
    }
}