package com.example.testjob

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import java.io.File

class OnboardingActivity : AppCompatActivity() {

    private lateinit var continueButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Проверяем, нужно ли показывать экран онбординга
        if (shouldShowOnboarding()) {
            // Показываем экран онбординга
            setContentView(R.layout.activity_onboarding)

            // val onboardingImage: ImageView = findViewById(R.id.onboardingImage)
            continueButton = findViewById(R.id.continueButton)

            continueButton.setOnClickListener {
                // Сохраняем информацию, что онбординг был показан
                markOnboardingAsShown()

                // Переход на страницу авторизации
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish() // Закрываем экран онбординга
            }
        } else {
            // Если онбординг уже был показан, проверяем, вошел ли пользователь
            if (isUserLoggedIn()) {
                // Если пользователь уже вошел, сразу переходим на главный экран
                goToMainActivity()
            } else {
                // Если пользователь не вошел, переходим на экран логина
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish() // Закрываем экран онбординга
            }
        }
    }

    private fun shouldShowOnboarding(): Boolean {
        // Проверяем наличие файла-маркера в кэше
        val markerFile = File(cacheDir, "onboarding_shown.marker")
        return !markerFile.exists()
    }

    private fun markOnboardingAsShown() {
        try {
            // Создаем файл-маркер в кэше
            val markerFile = File(cacheDir, "onboarding_shown.marker")
            markerFile.createNewFile()

            // Также сохраняем в SharedPreferences для надежности
            val sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
            sharedPreferences.edit().putBoolean("onboardingShown", true).apply()
        } catch (e: Exception) {
            Log.e("OnboardingActivity", "Error creating marker file", e)
        }
    }

    private fun isUserLoggedIn(): Boolean {
        val sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
        return sharedPreferences.getBoolean("isLoggedIn", false)
    }

    private fun goToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish() // Закрываем экран онбординга
    }
}



