package com.example.testjob

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var coursesRecyclerView: RecyclerView
    private lateinit var sortButton: ImageView
    private lateinit var searchView: SearchView

    private val coursesAdapter by lazy {
        CoursesAdapter(this, mutableListOf(), ::toggleFavorite)
    }

    private val client = OkHttpClient()
    private var coursesList: List<Course> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Инициализация интерфейса
        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        coursesRecyclerView = findViewById(R.id.recyclerView)
        sortButton = findViewById(R.id.sortButton)
        searchView = findViewById(R.id.searchEditText)

        // Устанавливаем экран "Главная" по умолчанию
        if (savedInstanceState == null) {
            replaceFragment(HomeFragment())
            bottomNavigationView.selectedItemId = R.id.nav_home
        }

        // Настройка BottomNavigationView
        setupBottomNavigation()

        // Настройка RecyclerView
        coursesRecyclerView.layoutManager = LinearLayoutManager(this)
        coursesRecyclerView.adapter = coursesAdapter

        // Загрузка данных с API
        fetchCourses()

        // Обработчик сортировки
        sortButton.setOnClickListener {
            coursesList = coursesList.sortedByDescending { it.publishDate }
            coursesAdapter.updateData(coursesList)
        }

        // Обработчик поиска
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val filteredCourses = coursesList.filter {
                    it.title.contains(newText ?: "", true) || it.text.contains(newText ?: "", true)
                }
                coursesAdapter.updateData(filteredCourses)
                return true
            }
        })
    }

    // Настройка BottomNavigationView
    private fun setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    // Показать RecyclerView и скрыть fragmentContainer
                    coursesRecyclerView.visibility = View.VISIBLE
                    searchView.visibility = View.VISIBLE
                    sortButton.visibility = View.VISIBLE
                    replaceFragment(HomeFragment())
                    true
                }
                R.id.nav_favorites -> {
                    // Показать фрагмент "Избранное" и скрыть RecyclerView
                    coursesRecyclerView.visibility = View.GONE
                    searchView.visibility = View.GONE
                    sortButton.visibility = View.GONE
                    replaceFragment(FavoritesFragment())
                    true
                }
                R.id.nav_account -> {
                    // Показать фрагмент "Аккаунт" и скрыть RecyclerView
                    coursesRecyclerView.visibility = View.GONE
                    searchView.visibility = View.GONE
                    sortButton.visibility = View.GONE
                    replaceFragment(AccountFragment())
                    true
                }
                else -> false
            }
        }
    }

    // Метод для замены фрагментов
    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    // Загрузка курсов с API
    private fun fetchCourses() {
        val request = Request.Builder()
            .url("https://drive.usercontent.google.com/u/0/uc?id=15arTK7XT2b7Yv4BJsmDctA4Hg-BbS8-q&export=download")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.string()?.let { responseBody ->
                    val coursesResponse = Gson().fromJson(responseBody, CoursesResponse::class.java)
                    coursesList = coursesResponse.courses

                    // Обновляем адаптер на главном потоке
                    runOnUiThread {
                        coursesAdapter.updateData(coursesList)
                    }
                }
            }
        })
    }

    // Обновление избранного
    private fun toggleFavorite(course: Course) {
        // Локальное обновление данных
        val updatedList = coursesList.toMutableList().map {
            if (it.id == course.id) {
                it.copy(hasLike = !it.hasLike)
            } else {
                it
            }
        }
        coursesList = updatedList
        coursesAdapter.updateData(coursesList)

        // TODO: Реализовать сохранение избранного в локальную базу данных
    }
}

// Модель ответа API
data class CoursesResponse(val courses: List<Course>)

// Расширение адаптера для обновления данных
fun CoursesAdapter.updateData(newData: List<Course>) {
    (this.courses as MutableList).clear()
    this.courses.addAll(newData)
    notifyDataSetChanged()
}