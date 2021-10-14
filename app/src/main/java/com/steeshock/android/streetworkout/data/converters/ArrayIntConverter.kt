package com.steeshock.android.streetworkout.data.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ArrayIntConverter {
    private var gson = Gson()

    @TypeConverter
    fun categoryListToString(someObjects: ArrayList<Int>?): String? {
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