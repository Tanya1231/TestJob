package com.example.testjob

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager

class OnboardingActivity : AppCompatActivity() {

    private lateinit var continueButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        val onboardingImage: ImageView = findViewById(R.id.onboardingImage)
        continueButton = findViewById(R.id.continueButton)

        continueButton.setOnClickListener {
            // Переход на страницу авторизации
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        // Проверка, показывать ли экран онбординга
        if (!isUserLoggedIn()) {
            continueButton.visibility = Button.VISIBLE
        } else {
            // Если пользователь уже вошел, сразу переходим на главный экран
            goToMainActivity()
        }
    }

    private fun isUserLoggedIn(): Boolean {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        return sharedPreferences.getBoolean("isLoggedIn", false)
    }

    private fun goToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish() // Закрываем онбординг экран
    }
}