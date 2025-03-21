package com.example.testjob

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorite_courses")
    suspend fun getAllFavorites(): List<FavoriteCourse>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(favoriteCourse: FavoriteCourse)

    @Query("DELETE FROM favorite_courses WHERE id = :courseId")
    suspend fun delete(courseId: Int)

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_courses WHERE id = :courseId LIMIT 1)")
    suspend fun isFavorite(courseId: Int): Boolean
}

