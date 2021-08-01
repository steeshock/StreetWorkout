package com.steeshock.android.streetworkout.utils

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.steeshock.android.streetworkout.data.model.Category
import java.util.*
import kotlin.collections.ArrayList

class ArrayStringConverter {
    private var gson = Gson()

    @TypeConverter
    fun imageListToString(someObjects: ArrayList<String>?): String? {
        return gson.toJson(someObjects)
    }

    @TypeConverter
    fun fromString(data: String?): ArrayList<String>? {

        if (data == null){
            return arrayListOf()
        }

        val listType = object : TypeToken<ArrayList<String>>() {}.type

        return gson.fromJson(data, listType)
    }
}