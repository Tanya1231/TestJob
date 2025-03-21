package com.example.testjob

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_courses")
data class FavoriteCourse(
    @PrimaryKey
    val id: Int,
    val title: String,
    val text: String,
    val price: String,
    val rate: Double,
    val startDate: String,
    val publishDate: String
) {
    companion object {
        fun fromCourse(course: Course): FavoriteCourse {
            return FavoriteCourse(
                id = course.id,
                title = course.title,
                text = course.text,
                price = course.price,
                rate = course.rate,
                startDate = course.startDate,
                publishDate = course.publishDate
            )
        }

        fun toCourse(favoriteCourse: FavoriteCourse): Course {
            return Course(
                id = favoriteCourse.id,
                title = favoriteCourse.title,
                text = favoriteCourse.text,
                price = favoriteCourse.price,
                rate = favoriteCourse.rate,
                startDate = favoriteCourse.startDate,
                hasLike = true, // Всегда true для избранных курсов
                publishDate = favoriteCourse.publishDate
            )
        }
    }
}