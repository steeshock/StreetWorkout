package com.example.android.streetworkout.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.android.streetworkout.data.model.PlaceObject


@Database(
    entities = [PlaceObject::class],
    version = 1,
    exportSchema = false
)
abstract class PlacesDatabase : RoomDatabase() {

    abstract fun getPlacesDao() : PlacesDao

    companion object {

        @Volatile
        private var INSTANCE: PlacesDatabase? = null

        fun getInstance(context: Context): PlacesDatabase {
            synchronized(this) {
                var instance =
                    INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        PlacesDatabase::class.java,
                        "places_database"
                    )
                        .allowMainThreadQueries()
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}