package com.example.testjob

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity


class LoginActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var registerButton: Button
    private lateinit var forgotPasswordButton: Button
    private lateinit var vkButton: ImageButton
    private lateinit var okButton: ImageButton

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

        // Находим кнопки
        try {
            registerButton = findViewById(R.id.registerButton)
            forgotPasswordButton = findViewById(R.id.forgotPasswordButton)
            vkButton = findViewById(R.id.vkButton)
            okButton = findViewById(R.id.okButton)

            // Делаем кнопки регистрации и восстановления пароля неактивными
            registerButton.isEnabled = false
            forgotPasswordButton.isEnabled = false

            // Обработчики для кнопок социальных сетей
            vkButton.setOnClickListener {
                openUrl("https://vk.com/")
            }

            okButton.setOnClickListener {
                openUrl("https://ok.ru/")
            }
        } catch (e: Exception) {
            // Если кнопки не найдены, просто игнорируем
            Log.e("LoginActivity", "Some buttons not found in layout", e)
        }

        // Устанавливаем InputFilter для запрета кириллицы в поле email
        emailEditText.filters = arrayOf(InputFilter { source, start, end, dest, dstart, dend ->
            for (i in start until end) {
                if (source[i].toString().matches(Regex("[а-яА-Я]"))) {
                    return@InputFilter ""
                }
            }
            null
        })

        // Устанавливаем TextWatcher для проверки ввода
        emailEditText.addTextChangedListener(textWatcher)
        passwordEditText.addTextChangedListener(textWatcher)

        // Обработчик нажатия на кнопку входа
        loginButton.setOnClickListener {
            // Сохранение состояния входа
            val sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
            sharedPreferences.edit().putBoolean("isLoggedIn", true).apply()

            // Переход на главный экран
            goToMainActivity()
        }

        // Начальная проверка полей
        validateInput()
    }

    private fun validateInput() {
        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()

        // Проверка на валидный email
        val emailPattern =
            Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", RegexOption.IGNORE_CASE)
        val isEmailValid = emailPattern.matches(email)

        // Проверка на заполненность полей
        loginButton.isEnabled = isEmailValid && password.isNotEmpty()
    }

    private fun openUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
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