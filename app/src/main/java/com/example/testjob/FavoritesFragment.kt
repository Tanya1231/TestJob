package com.example.testjob

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FavoritesFragment : Fragment() {
    private lateinit var favoritesRecyclerView: RecyclerView
    private lateinit var emptyView: TextView
    private lateinit var database: CourseDatabase
    private lateinit var favoritesAdapter: FavoritesAdapter
    private val coroutineScope = CoroutineScope(Dispatchers.Main + Job())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_favorites, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        database = CourseDatabase.getDatabase(requireContext())

        favoritesRecyclerView = view.findViewById(R.id.favoritesRecyclerView)
        emptyView = view.findViewById(R.id.emptyFavoritesView)

        favoritesRecyclerView.layoutManager = LinearLayoutManager(context)

        favoritesAdapter = FavoritesAdapter(mutableListOf(), ::toggleFavorite)
        favoritesRecyclerView.adapter = favoritesAdapter

        loadFavorites()
    }

    override fun onResume() {
        super.onResume()
        loadFavorites()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        coroutineScope.cancel()
    }

    private fun loadFavorites() {
        coroutineScope.launch {
            try {
                // Получаем все избранные курсы
                val favorites = withContext(Dispatchers.IO) {
                    val dbFavorites = database.favoriteDao().getAllFavorites()
                    dbFavorites.map { FavoriteCourse.toCourse(it) }
                }

                // Обновляем UI на главном потоке
                withContext(Dispatchers.Main) {
                    favoritesAdapter.updateData(favorites)

                    // Показываем сообщение, если нет избранных курсов
                    if (favorites.isEmpty()) {
                        emptyView.visibility = View.VISIBLE
                        favoritesRecyclerView.visibility = View.GONE
                    } else {
                        emptyView.visibility = View.GONE
                        favoritesRecyclerView.visibility = View.VISIBLE
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "Ошибка загрузки избранного: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }


    private fun toggleFavorite(course: Course) {
        coroutineScope.launch {
            try {
                // Удаляем курс из избранного
                withContext(Dispatchers.IO) {
                    database.favoriteDao().delete(course.id)
                }

                // Обновляем список избранного
                loadFavorites()

                // Показываем уведомление
                Toast.makeText(
                    requireContext(),
                    "Курс удален из избранного",
                    Toast.LENGTH_SHORT
                ).show()
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "Ошибка удаления из избранного: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}


