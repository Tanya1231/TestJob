package com.example.testjob

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class CoursesAdapter(
    private var courses: MutableList<Course>,
    private val onFavoriteToggle: (Course) -> Unit
) : RecyclerView.Adapter<CoursesAdapter.CourseViewHolder>() {

    inner class CourseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTextView: TextView = view.findViewById(R.id.titleTextView)
        val descriptionTextView: TextView = view.findViewById(R.id.descriptionTextView)
        val priceTextView: TextView = view.findViewById(R.id.priceTextView)
        val rateTextView: TextView = view.findViewById(R.id.rateTextView)
        val startDateTextView: TextView = view.findViewById(R.id.startDateTextView)
        val favoriteIcon: ImageView = view.findViewById(R.id.favoriteIcon)
        val cardView: CardView = view.findViewById(R.id.courseCardView)
        val courseImageView: ImageView = view.findViewById(R.id.courseImageView)

        fun bind(course: Course) {
            titleTextView.text = course.title
            descriptionTextView.text = course.text
            priceTextView.text = "Цена: ${course.price}"
            rateTextView.text = "Рейтинг: ${course.rate}"
            startDateTextView.text = "Начало: ${course.startDate}"

            descriptionTextView.maxLines = 2
            descriptionTextView.ellipsize = android.text.TextUtils.TruncateAt.END

            favoriteIcon.setColorFilter(if (course.hasLike) Color.GREEN else Color.GRAY)

            // Генерируем URL изображения на основе ID курса
            val imageUrl = "https://placeimg.com/800/400/tech?${course.id}"

            // Загружаем изображение с помощью Glide
            Glide.with(itemView.context)
                .load(imageUrl)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.students)
                .centerCrop()
                .into(courseImageView)

            favoriteIcon.setOnClickListener {
                onFavoriteToggle(course)
                notifyItemChanged(adapterPosition)
            }

            cardView.setOnClickListener {
                // Здесь можно добавить обработку нажатия на карточку курса
                // Например, открытие детальной информации о курсе
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

    override fun getItemCount(): Int {
        return courses.size
    }

    fun updateData(newCourses: List<Course>) {
        courses.clear()
        courses.addAll(newCourses)
        notifyDataSetChanged()
    }
}

