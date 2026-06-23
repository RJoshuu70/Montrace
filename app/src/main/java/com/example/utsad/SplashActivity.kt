package com.example.utsad

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

class SplashActivity : AppCompatActivity() {

    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        // Install SplashScreen API sebelum super.onCreate
        installSplashScreen()
        
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Runnable: navigasi ke OnboardingActivity setelah SPLASH_DELAY_MS
        val navigateToOnboarding = Runnable {
            // Intent eksplisit: langsung menyebutkan kelas tujuan
            val intent = Intent(this@SplashActivity, OnboardingActivity::class.java)
            startActivity(intent)
            // Hapus SplashActivity dari back stack agar tidak bisa di-back ke sini
            finish()
        }

        // Post delay: eksekusi Runnable setelah 2 detik
        handler.postDelayed(navigateToOnboarding, SPLASH_DELAY_MS)
    }

    override fun onDestroy() {
        super.onDestroy()
        // Penting: hapus semua callback agar tidak memory leak jika Activity di-destroy sebelum delay selesai
        handler.removeCallbacksAndMessages(null)
    }

    companion object {
        private const val SPLASH_DELAY_MS = 2000L
    }
}
