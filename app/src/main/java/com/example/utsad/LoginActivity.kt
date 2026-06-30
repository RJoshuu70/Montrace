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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * LoginActivity — Fase 2 (Auth Screen) sesuai implementation plan.
 *
 * Alur:
 * 1. Validasi input di client side dulu (fail fast, tidak perlu query DB kalau field kosong).
 * 2. Cari user berdasarkan email lewat UserDao (memanfaatkan unique index pada kolom email).
 * 3. Cocokkan password secara manual (Room tidak melakukan hashing otomatis).
 * 4. Jika valid → simpan sesi via SessionManager, lalu pindah ke MainActivity dengan
 *    membersihkan back stack (user tidak boleh bisa "back" ke layar Login setelah masuk).
 */
class LoginActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var tvError: TextView
    private lateinit var tvGoToSignup: TextView
    private lateinit var progressBar: ProgressBar

    private lateinit var database: AppDatabase
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        database = AppDatabase.getDatabase(this)
        sessionManager = SessionManager(this)

        initViews()
        setupListeners()
    }

    private fun initViews() {
        etEmail = findViewById(R.id.et_login_email)
        etPassword = findViewById(R.id.et_login_password)
        btnLogin = findViewById(R.id.btn_login)
        tvError = findViewById(R.id.tv_login_error)
        tvGoToSignup = findViewById(R.id.tv_go_to_signup)
        progressBar = findViewById(R.id.progress_login)
    }

    private fun setupListeners() {
        btnLogin.setOnClickListener { attemptLogin() }

        // Navigasi ke SignupActivity. finish() TIDAK dipanggil di sini supaya
        // tombol back dari Signup mengembalikan user ke Login, bukan langsung keluar app.
        tvGoToSignup.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }

    private fun attemptLogin() {
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString()

        val validationError = validate(email, password)
        if (validationError != null) {
            showError(validationError)
            return
        }

        setLoading(true)
        lifecycleScope.launch {
            // Operasi database WAJIB di-dispatch ke Dispatchers.IO agar tidak memblokir main/UI thread.
            val user = withContext(Dispatchers.IO) {
                database.userDao().getUserByEmail(email)
            }
            setLoading(false)

            if (user == null || user.password != password) {
                // Pesan generik (tidak membedakan "email tidak ada" vs "password salah")
                // adalah praktik keamanan standar agar tidak membantu attacker melakukan user enumeration.
                showError("Email atau password salah.")
                return@launch
            }

            sessionManager.saveSession(userId = user.id, name = user.name, email = user.email)
            navigateToMain()
        }
    }

    private fun validate(email: String, password: String): String? {
        return when {
            email.isEmpty() -> "Email tidak boleh kosong."
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Format email tidak valid."
            password.isEmpty() -> "Password tidak boleh kosong."
            else -> null
        }
    }

    private fun showError(message: String) {
        tvError.text = message
        tvError.visibility = View.VISIBLE
    }

    private fun setLoading(isLoading: Boolean) {
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        btnLogin.isEnabled = !isLoading
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
