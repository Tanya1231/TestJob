package com.example.testjob

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class OnboardingActivity : AppCompatActivity() {

    private lateinit var appPreferences: AppPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        // Инициализируем AppPreferences
        appPreferences = AppPreferences(this)

        // Настройка UI с видимой строкой состояния
        setupUIWithStatusBar()

        // Настройка кнопки
        val continueButton = findViewById<Button>(R.id.continueButton)
        continueButton.setOnClickListener {
            // Сохраняем информацию о том, что онбординг пройден
            appPreferences.setOnboardingCompleted()

            // Переходим на экран логина
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun setupUIWithStatusBar() {
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
            setupUIWithStatusBar()
        }
    }
}




