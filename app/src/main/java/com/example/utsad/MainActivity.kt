package com.example.utsad

import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var bottomNavigation: BottomNavigationView

    private lateinit var homeFragment: HomeFragment
    private lateinit var transactionFragment: TransactionFragment
    private lateinit var overviewFragment: OverviewFragment
    private lateinit var aboutFragment: AboutFragment

    // Referensi fragment yang sedang aktif
    private lateinit var activeFragment: Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        applyStatusBarInset()
        initFragments()
        setupBottomNavigation()
    }

    /**
     * Read the real status bar height from WindowInsets and apply it as
     * paddingTop on the fragment container so every fragment sits below
     * the status bar — no hardcoded dp values needed in fragment layouts.
     */
    private fun applyStatusBarInset() {
        val container = findViewById<FrameLayout>(R.id.fragment_container)
        applyStatusBarInset(container)
    }

    private fun initViews() {
        bottomNavigation = findViewById(R.id.bottom_navigation)
    }

    private fun initFragments() {
        homeFragment = HomeFragment()
        transactionFragment = TransactionFragment()
        overviewFragment = OverviewFragment()
        aboutFragment = AboutFragment()

        // Set HomeFragment sebagai tampilan awal
        activeFragment = homeFragment
        supportFragmentManager
            .beginTransaction()
            .add(R.id.fragment_container, aboutFragment, "about").hide(aboutFragment)
            .add(R.id.fragment_container, overviewFragment, "overview").hide(overviewFragment)
            .add(R.id.fragment_container, transactionFragment, "transaction").hide(transactionFragment)
            .add(R.id.fragment_container, homeFragment, "home")
            .commit()
    }

    private fun setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    loadFragment(homeFragment)
                    true
                }
                R.id.nav_transaction -> {
                    loadFragment(transactionFragment)
                    true
                }
                R.id.nav_overview -> {
                    loadFragment(overviewFragment)
                    true
                }
                R.id.nav_about -> {
                    loadFragment(aboutFragment)
                    true
                }
                else -> false
            }
        }

        // Pastikan item Home terseleksi saat pertama kali
        bottomNavigation.selectedItemId = R.id.nav_home
    }

    private fun loadFragment(targetFragment: Fragment) {
        if (targetFragment !== activeFragment) {
            supportFragmentManager
                .beginTransaction()
                .hide(activeFragment)
                .show(targetFragment)
                .commit()
            activeFragment = targetFragment
        }
    }

    companion object {
        fun applyStatusBarInset(container: View) {
            ViewCompat.setOnApplyWindowInsetsListener(container) { v, insets ->
                val statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
                v.setPadding(
                    v.paddingLeft,
                    statusBarHeight,
                    v.paddingRight,
                    v.paddingBottom
                )
                insets
            }
        }
    }
}
