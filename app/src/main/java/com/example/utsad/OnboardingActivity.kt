package com.example.utsad

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class OnboardingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        // Referensi tombol Next dari layout
        val btnNext = findViewById<Button>(R.id.btn_next)

        // Event listener: saat tombol Next ditekan
        btnNext.setOnClickListener { navigateToMain() }
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java).apply {
            // Bersihkan back stack: user tidak bisa back ke splash/onboarding
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
        startActivity(intent)
        finish()
    }
}
