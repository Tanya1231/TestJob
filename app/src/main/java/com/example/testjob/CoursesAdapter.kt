package com.example.testjob

import android.graphics.Color
import android.graphics.Shader
import android.os.Build
import android.graphics.RenderEffect
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.util.Locale
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
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
        val detailsButton: LinearLayout = view.findViewById(R.id.detailsButton)
        val ratingContainer: LinearLayout = view.findViewById(R.id.ratingContainer)
        val dateContainer: LinearLayout = view.findViewById(R.id.dateContainer)
        val favoriteContainer: LinearLayout = view.findViewById(R.id.favoriteContainer)

        fun bind(course: Course) {
            titleTextView.text = course.title
            descriptionTextView.text = course.text
            priceTextView.text = "${course.price} ₽"
            rateTextView.text = "${course.rate}"

            val formattedDate = formatDate(course.startDate)
            startDateTextView.text = formattedDate

            descriptionTextView.maxLines = 2
            descriptionTextView.ellipsize = android.text.TextUtils.TruncateAt.END

            if (course.hasLike) {
                favoriteIcon.setImageResource(R.drawable.ic_favorites_green)
                favoriteIcon.setColorFilter(Color.GREEN)
            } else {
                favoriteIcon.setImageResource(R.drawable.ic_favorite_small)
                favoriteIcon.setColorFilter(Color.WHITE)
            }

            // Эффект размытия для Android 12+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                applyBlurEffect(ratingContainer)
                applyBlurEffect(dateContainer)
                applyBlurEffect(favoriteContainer)
            }

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
            }

            detailsButton.setOnClickListener {
                // Обработка нажатия на кнопку "Подробнее"
            }
        }

        // Функция для форматирования даты
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

        // Функция для применения эффекта размытия
        @RequiresApi(Build.VERSION_CODES.S)
        private fun applyBlurEffect(view: View) {
            val blurEffect = RenderEffect.createBlurEffect(
                16f, // радиус размытия
                16f, // радиус размытия
                Shader.TileMode.CLAMP
            )
            view.setRenderEffect(blurEffect)
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

