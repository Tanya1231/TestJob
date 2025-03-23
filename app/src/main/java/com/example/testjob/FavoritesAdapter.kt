package com.example.testjob

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.Locale

class FavoritesAdapter(
    private var courses: MutableList<Course>,
    private val onFavoriteToggle: (Course) -> Unit
) : RecyclerView.Adapter<FavoritesAdapter.FavoriteViewHolder>() {

    inner class FavoriteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTextView: TextView = view.findViewById(R.id.titleTextView)
        val descriptionTextView: TextView = view.findViewById(R.id.descriptionTextView)
        val priceTextView: TextView = view.findViewById(R.id.priceTextView)
        val rateTextView: TextView = view.findViewById(R.id.rateTextView)
        val startDateTextView: TextView = view.findViewById(R.id.startDateTextView)
        val favoriteIcon: ImageView = view.findViewById(R.id.favoriteIcon)
        val courseImageView: ImageView = view.findViewById(R.id.courseImageView)
        val detailsButton: LinearLayout = view.findViewById(R.id.detailsButton)

        fun bind(course: Course) {
            titleTextView.text = course.title
            descriptionTextView.text = course.text
            priceTextView.text = "${course.price} ₽"
            rateTextView.text = "${course.rate}"

            val formattedDate = formatDate(course.startDate)
            startDateTextView.text = formattedDate

            favoriteIcon.setImageResource(R.drawable.ic_favorites_green)
            favoriteIcon.setColorFilter(Color.GREEN)

            // Генерируем URL изображения
            val imageUrl = "https://placeimg.com/800/400/tech?${course.id}"

            // Загружаем изображение с помощью Glide
            Glide.with(itemView.context)
                .load(imageUrl)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .centerCrop()
                .into(courseImageView)

            favoriteIcon.setOnClickListener {
                onFavoriteToggle(course)
                notifyItemRemoved(adapterPosition)
            }

            detailsButton.setOnClickListener {
                // Обработка нажатия на кнопку "Подробнее"
            }
        }

        private fun formatDate(dateString: String): String {
            try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val date = inputFormat.parse(dateString) ?: return dateString

                val outputFormat = SimpleDateFormat("dd MMMM yyyy", Locale("ru"))
                return outputFormat.format(date)
            } catch (e: Exception) {
                return dateString
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_course, parent, false)
        return FavoriteViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
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

