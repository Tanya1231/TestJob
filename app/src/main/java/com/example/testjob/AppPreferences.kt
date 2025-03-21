package com.example.testjob

import android.content.Context
import java.io.File

class AppPreferences(private val context: Context) {

    companion object {
        private const val ONBOARDING_FILE = "onboarding_completed"
        private const val LOGIN_FILE = "user_logged_in"
    }

    // Проверяет, пройден ли онбординг
    fun isOnboardingCompleted(): Boolean {
        val file = File(context.cacheDir, ONBOARDING_FILE)
        return file.exists()
    }

    // Сохраняет информацию о прохождении онбординга
    fun setOnboardingCompleted() {
        val file = File(context.cacheDir, ONBOARDING_FILE)
        file.createNewFile()
    }

    // Проверяет, вошел ли пользователь
    fun isUserLoggedIn(): Boolean {
        val file = File(context.cacheDir, LOGIN_FILE)
        return file.exists()
    }

    // Сохраняет информацию о входе пользователя
    fun setUserLoggedIn() {
        val file = File(context.cacheDir, LOGIN_FILE)
        file.createNewFile()
    }

    // Очищает все настройки
    fun clearAll() {
        val onboardingFile = File(context.cacheDir, ONBOARDING_FILE)
        val loginFile = File(context.cacheDir, LOGIN_FILE)

        if (onboardingFile.exists()) onboardingFile.delete()
        if (loginFile.exists()) loginFile.delete()
    }
}
