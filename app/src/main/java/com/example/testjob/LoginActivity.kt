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
    private lateinit var appPreferences: AppPreferences

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

        // Инициализируем AppPreferences
        appPreferences = AppPreferences(this)

        // Проверяем, нужно ли показывать онбординг
        if (!appPreferences.isOnboardingCompleted()) {
            startActivity(Intent(this, OnboardingActivity::class.java))
            finish()
            return
        }

        // Проверяем, вошел ли пользователь
        if (appPreferences.isUserLoggedIn()) {
            goToMainActivity()
            return
        }

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
            // Сохраняем состояние входа
            appPreferences.setUserLoggedIn()

            // Переход на главный экран
            goToMainActivity()
        }

        // Начальная проверка полей
        validateInput()
    }

    private fun checkOnboardingAndLogin() {
        val sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val isOnboardingCompleted = sharedPreferences.getBoolean("onboarding_completed", false)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)

        // Проверяем, есть ли в SharedPreferences запись об онбординге
        // Если нет, значит это первый запуск или данные были очищены
        if (!isOnboardingCompleted) {
            // Если онбординг не пройден, показываем его
            startActivity(Intent(this, OnboardingActivity::class.java))
            finish()
            return
        }

        // Если онбординг пройден, но пользователь не вошел, остаемся на экране логина
        if (isLoggedIn) {
            // Если пользователь уже вошел, сразу переходим на главный экран
            goToMainActivity()
            return
        }

        // В противном случае остаемся на экране логина
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

