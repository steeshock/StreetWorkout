package com.steeshock.streetworkout.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.steeshock.streetworkout.data.converters.ArrayIntConverter
import com.steeshock.streetworkout.data.converters.ListStringConverter
import com.steeshock.streetworkout.data.model.Category
import com.steeshock.streetworkout.data.model.Place
import com.steeshock.streetworkout.data.model.UserDto


@Database(
    entities = [Place::class, Category::class, UserDto::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(ArrayIntConverter::class, ListStringConverter::class)
abstract class PlacesDatabase : RoomDatabase() {

    abstract fun getPlacesDao(): PlacesDao
    abstract fun getCategoriesDao(): CategoriesDao
    abstract fun getUserDao(): UserDao

    companion object {

        @Volatile
        private var instance: PlacesDatabase? = null

        fun getInstance(context: Context): PlacesDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): PlacesDatabase {
            return Room.databaseBuilder(context, PlacesDatabase::class.java, "places_database")
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}