package com.steeshock.android.streetworkout.utils

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
//
//        val forceDataArray = stringToValues(data)
//
//        var modifiedData: String? = null;
//        if (forceDataArray.size == 1){
//            modifiedData = "[$data]"
//        }
//
        val listType = object : TypeToken<ArrayList<Int>>() {}.type
//
//        return if (modifiedData?.isEmpty() == false){
//            gson.fromJson(modifiedData, listType)
//        } else {
//            gson.fromJson(data, listType)
//        }

        return gson.fromJson(data, listType)
    }

    private fun stringToValues(s : String) = s.trim { i -> i == '[' || i == ']'}
        .splitToSequence(',')
        .filter { it.isNotEmpty() }
        .toMutableList()
}