package com.example.testjob

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class Course(
    val id: Int,
    val title: String,
    val text: String,
    val price: String,
    val rate: Double,
    val startDate: String,
    val hasLike: Boolean,
    val publishDate: String
)

class CoursesAdapter(
    private val context: Context,
    val courses: List<Course>,
    private val onFavoriteToggle: (Course) -> Unit
) : RecyclerView.Adapter<CoursesAdapter.CourseViewHolder>() {

    inner class CourseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTextView: TextView = view.findViewById(R.id.titleTextView)
        val descriptionTextView: TextView = view.findViewById(R.id.descriptionTextView)
        val priceTextView: TextView = view.findViewById(R.id.priceTextView)
        val rateTextView: TextView = view.findViewById(R.id.rateTextView)
        val favoriteIcon: ImageView = view.findViewById(R.id.favoriteIcon)

        fun bind(course: Course) {
            titleTextView.text = course.title
            descriptionTextView.text = course.text
            priceTextView.text = "Цена: ${course.price}"
            rateTextView.text = "Рейтинг: ${course.rate}"

            // Обрезка описания до 2 строк
            descriptionTextView.maxLines = 2
            descriptionTextView.ellipsize = android.text.TextUtils.TruncateAt.END

            // Установка цвета для избранного
            if (course.hasLike) {
                favoriteIcon.setColorFilter(Color.GREEN)
            } else {
                favoriteIcon.setColorFilter(Color.GRAY)
            }

            // Обработка клика на иконку избранного
            favoriteIcon.setOnClickListener {
                onFavoriteToggle(course)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_course, parent, false)
        return CourseViewHolder(view)
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        holder.bind(courses[position])
    }

    override fun getItemCount(): Int = courses.size
}