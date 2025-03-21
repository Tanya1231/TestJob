package com.example.testjob

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var coursesRecyclerView: RecyclerView
    private lateinit var sortButton: MaterialButton
    private lateinit var searchView: SearchView
    private lateinit var fragmentContainer: FrameLayout
    private lateinit var appPreferences: AppPreferences

    private val coursesAdapter by lazy {
        CoursesAdapter(mutableListOf(), ::toggleFavorite)
    }

    private var coursesList: List<Course> = listOf()
    private lateinit var database: CourseDatabase
    private val coroutineScope = CoroutineScope(Dispatchers.Main + Job())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Инициализируем AppPreferences
        appPreferences = AppPreferences(this)

        // Проверяем, пройден ли онбординг и вошел ли пользователь
        if (!appPreferences.isOnboardingCompleted()) {
            startActivity(Intent(this, OnboardingActivity::class.java))
            finish()
            return
        }

        if (!appPreferences.isUserLoggedIn()) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        // Устанавливаем layout
        setContentView(R.layout.activity_main)

        // Инициализация базы данных
        database = CourseDatabase.getDatabase(this)

        // Инициализация views
        initViews()

        // Показываем только строку состояния, скрываем навигационную панель
        showStatusBarHideNavBar()

        if (savedInstanceState == null) {
            bottomNavigationView.selectedItemId = R.id.nav_home
        }

        setupBottomNavigation()
        setupRecyclerView()
        setupListeners()
        fetchCourses()
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }

    private fun showStatusBarHideNavBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Для Android 11 (API 30) и выше
            window.setDecorFitsSystemWindows(false)
            window.insetsController?.let {
                // Скрываем только навигационную панель, оставляя строку состояния видимой
                it.hide(WindowInsets.Type.navigationBars())
                it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            // Для Android 10 (API 29) и ниже
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    // Убираем флаг SYSTEM_UI_FLAG_FULLSCREEN, чтобы показать строку состояния
                    )
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            showStatusBarHideNavBar()
        }
    }

    private fun initViews() {
        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        coursesRecyclerView = findViewById(R.id.recyclerView)
        sortButton = findViewById(R.id.sortButton)
        searchView = findViewById(R.id.searchEditText)
        fragmentContainer = findViewById(R.id.fragment_container)
    }

    private fun setupRecyclerView() {
        coursesRecyclerView.layoutManager = LinearLayoutManager(this)
        coursesRecyclerView.adapter = coursesAdapter
    }

    private fun setupListeners() {
        sortButton.setOnClickListener {
            coursesList = coursesList.sortedByDescending { it.publishDate }
            coursesAdapter.updateData(coursesList)
        }

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

    private fun setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    fragmentContainer.visibility = View.GONE
                    coursesRecyclerView.visibility = View.VISIBLE
                    searchView.visibility = View.VISIBLE
                    sortButton.visibility = View.VISIBLE
                    true
                }

                R.id.nav_favorites -> {
                    fragmentContainer.visibility = View.VISIBLE
                    coursesRecyclerView.visibility = View.GONE
                    searchView.visibility = View.GONE
                    sortButton.visibility = View.GONE
                    showFavorites()
                    true
                }

                R.id.nav_account -> {
                    fragmentContainer.visibility = View.VISIBLE
                    coursesRecyclerView.visibility = View.GONE
                    searchView.visibility = View.GONE
                    sortButton.visibility = View.GONE
                    showAccount()
                    true
                }

                else -> false
            }
        }
    }

    private fun fetchCourses() {
        RetrofitClient.courseApiService.getCourses()
            .enqueue(object : retrofit2.Callback<CoursesResponse> {
                override fun onFailure(call: retrofit2.Call<CoursesResponse>, t: Throwable) {
                    t.printStackTrace()
                    runOnUiThread {
                        Toast.makeText(
                            this@MainActivity,
                            "Ошибка загрузки курсов: ${t.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onResponse(
                    call: retrofit2.Call<CoursesResponse>,
                    response: retrofit2.Response<CoursesResponse>
                ) {
                    if (!response.isSuccessful) {
                        runOnUiThread {
                            Toast.makeText(
                                this@MainActivity,
                                "Ошибка сервера: ${response.code()}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        return
                    }

                    response.body()?.let { coursesResponse ->
                        try {
                            val courses = coursesResponse.courses

                            // Проверяем, какие курсы уже в избранном
                            coroutineScope.launch {
                                val updatedCourses = courses.map { course ->
                                    val isFavorite = withContext(Dispatchers.IO) {
                                        database.favoriteDao().isFavorite(course.id)
                                    }
                                    course.copy(hasLike = isFavorite)
                                }

                                coursesList = updatedCourses

                                withContext(Dispatchers.Main) {
                                    coursesAdapter.updateData(coursesList)
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            runOnUiThread {
                                Toast.makeText(
                                    this@MainActivity,
                                    "Ошибка парсинга данных: ${e.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                }
            })
    }

    private fun toggleFavorite(course: Course) {
        val updatedList = coursesList.toMutableList().map {
            if (it.id == course.id) {
                it.copy(hasLike = !it.hasLike)
            } else {
                it
            }
        }
        coursesList = updatedList
        coursesAdapter.updateData(coursesList)

        // Сохранение избранного в локальную базу данных
        coroutineScope.launch {
            try {
                val isFavorite = withContext(Dispatchers.IO) {
                    database.favoriteDao().isFavorite(course.id)
                }

                if (isFavorite) {
                    // Удаляем из избранного
                    withContext(Dispatchers.IO) {
                        database.favoriteDao().delete(course.id)
                    }
                    Toast.makeText(
                        this@MainActivity,
                        "Курс удален из избранного",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    // Добавляем в избранное
                    withContext(Dispatchers.IO) {
                        val favCourse = FavoriteCourse.fromCourse(course)
                        database.favoriteDao().insert(favCourse)
                    }

                    Toast.makeText(
                        this@MainActivity,
                        "Курс добавлен в избранное",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "Ошибка: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showFavorites() {
        val favoritesFragment = FavoritesFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, favoritesFragment)
            .commit()
    }

    private fun showAccount() {
        val accountFragment = AccountFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, accountFragment)
            .commit()
    }
}




