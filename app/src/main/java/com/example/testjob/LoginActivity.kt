package com.example.testjob

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit


    class LoginActivity : AppCompatActivity() {

        private lateinit var emailEditText: EditText
        private lateinit var passwordEditText: EditText
        private lateinit var loginButton: Button

        // Объявляем TextWatcher, чтобы потом удалять его при уничтожении Activity
        private val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                validateInput()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_login)

            // Инициализация полей
            emailEditText = findViewById(R.id.emailEditText)
            passwordEditText = findViewById(R.id.passwordEditText)
            loginButton = findViewById(R.id.loginButton)

            // Устанавливаем TextWatcher для проверки ввода
            emailEditText.addTextChangedListener(textWatcher)
            passwordEditText.addTextChangedListener(textWatcher)

            // Обработчик нажатия на кнопку входа
            loginButton.setOnClickListener {
                // Сохранение состояния входа
                val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
                sharedPreferences.edit { putBoolean("isLoggedIn", true) }

                // Переход на главный экран
                goToMainActivity()
            }
        }

        private fun validateInput() {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            // Проверка на валидный email
            val emailPattern = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z]{2,}$", RegexOption.IGNORE_CASE)
            val isEmailValid = emailPattern.matches(email)

            // Проверка на заполненность полей
            loginButton.isEnabled = isEmailValid && password.isNotEmpty()
        }

        private fun goToMainActivity() {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Закрываем экран авторизации
        }

        override fun onDestroy() {
            super.onDestroy()
            // Удаляем TextWatcher, чтобы избежать утечек памяти
            emailEditText.removeTextChangedListener(textWatcher)
            passwordEditText.removeTextChangedListener(textWatcher)
        }
    }