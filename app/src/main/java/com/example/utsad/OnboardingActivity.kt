package com.example.utsad

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.utsad.data.SessionManager

class OnboardingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Jika user sudah pernah login sebelumnya (session masih tersimpan di SharedPreferences),
        // langsung lompat ke MainActivity tanpa perlu menampilkan Onboarding/Login lagi.
        // Ini bagian dari "Session Management" yang diminta di Fase 2.
        if (SessionManager(this).isLoggedIn()) {
            navigateToMain()
            return
        }

        setContentView(R.layout.activity_onboarding)

        // Referensi tombol Next dari layout
        val btnNext = findViewById<Button>(R.id.btn_next)

        // Event listener: saat tombol Next ditekan, arahkan ke Login (bukan langsung MainActivity)
        btnNext.setOnClickListener { navigateToLogin() }
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java).apply {
            // Bersihkan back stack: user tidak bisa back ke onboarding setelah masuk alur auth
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
        startActivity(intent)
        finish()
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
        startActivity(intent)
        finish()
    }
}

