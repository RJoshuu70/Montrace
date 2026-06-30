package com.example.utsad

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.utsad.data.AppDatabase
import com.example.utsad.data.SessionManager
import com.example.utsad.data.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * SignupActivity — Fase 2 (Auth Screen) sesuai implementation plan.
 *
 * Alur:
 * 1. Validasi input di client side (nama, format email, panjang minimum password).
 * 2. INSERT ke tabel users lewat UserDao.insertUser(), yang sudah dikonfigurasi
 *    dengan OnConflictStrategy.IGNORE oleh Torikh di Fase 1.
 * 3. Karena IGNORE, insert ke email yang sudah terdaftar tidak melempar exception,
 *    melainkan mengembalikan -1L. Kondisi inilah yang kita pakai untuk mendeteksi duplikat.
 * 4. Jika sukses (id != -1) → langsung auto-login (simpan sesi) lalu ke MainActivity,
 *    konsisten dengan pola Splash→Onboarding yang juga auto-skip layar yang sudah dilalui.
 */
class SignupActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnSignup: Button
    private lateinit var tvError: TextView
    private lateinit var tvGoToLogin: TextView
    private lateinit var progressBar: ProgressBar

    private lateinit var database: AppDatabase
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        database = AppDatabase.getDatabase(this)
        sessionManager = SessionManager(this)

        initViews()
        setupListeners()
    }

    private fun initViews() {
        etName = findViewById(R.id.et_signup_name)
        etEmail = findViewById(R.id.et_signup_email)
        etPassword = findViewById(R.id.et_signup_password)
        btnSignup = findViewById(R.id.btn_signup)
        tvError = findViewById(R.id.tv_signup_error)
        tvGoToLogin = findViewById(R.id.tv_go_to_login)
        progressBar = findViewById(R.id.progress_signup)
    }

    private fun setupListeners() {
        btnSignup.setOnClickListener { attemptSignup() }
        tvGoToLogin.setOnClickListener {
            // finish() supaya tidak menumpuk Login -> Signup -> Login di back stack.
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun attemptSignup() {
        val name = etName.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString()

        val validationError = validate(name, email, password)
        if (validationError != null) {
            showError(validationError)
            return
        }

        setLoading(true)
        lifecycleScope.launch {
            val newUserId = withContext(Dispatchers.IO) {
                database.userDao().insertUser(User(name = name, email = email, password = password))
            }
            setLoading(false)

            if (newUserId == -1L) {
                showError("Email ini sudah terdaftar. Silakan log in.")
                return@launch
            }

            sessionManager.saveSession(userId = newUserId.toInt(), name = name, email = email)
            navigateToMain()
        }
    }

    private fun validate(name: String, email: String, password: String): String? {
        return when {
            name.isEmpty() -> "Nama lengkap tidak boleh kosong."
            email.isEmpty() -> "Email tidak boleh kosong."
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Format email tidak valid."
            password.length < 6 -> "Password minimal 6 karakter."
            else -> null
        }
    }

    private fun showError(message: String) {
        tvError.text = message
        tvError.visibility = View.VISIBLE
    }

    private fun setLoading(isLoading: Boolean) {
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        btnSignup.isEnabled = !isLoading
        if (isLoading) tvError.visibility = View.GONE
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
        startActivity(intent)
        finish()
    }
}
