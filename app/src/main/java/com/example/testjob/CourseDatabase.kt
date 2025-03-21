package com.example.testjob

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [FavoriteCourse::class], version = 1, exportSchema = false)
abstract class CourseDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao

    companion object {
        @Volatile
        private var INSTANCE: CourseDatabase? = null

        fun getDatabase(context: Context): CourseDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CourseDatabase::class.java,
                    "course_database"
                ).build()
                INSTANCE = instance
                Log.d("CourseDatabase", "Database initialized")
                instance
            }
        }
    }
}