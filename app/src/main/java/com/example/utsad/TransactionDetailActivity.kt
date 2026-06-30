package com.example.utsad

import android.graphics.Color
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat

class TransactionDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Make activity draw under system bars
        WindowCompat.setDecorFitsSystemWindows(window, false)
        // Make status bar transparent so root background shows through
        window.statusBarColor = Color.TRANSPARENT
        
        setContentView(R.layout.activity_transaction_detail)

        // Apply status bar padding to the root view
        MainActivity.applyStatusBarInset(findViewById(R.id.root_detail))

        val ivBack = findViewById<ImageView>(R.id.iv_back)
        val tvCategory = findViewById<TextView>(R.id.tv_detail_category)
        val tvSource = findViewById<TextView>(R.id.tv_detail_source)
        val tvAmount = findViewById<TextView>(R.id.tv_detail_amount)

        // Handle Back Navigation
        ivBack.setOnClickListener { finish() }

        val tvDateDetail = findViewById<TextView>(R.id.tv_date_detail)
        val dateFormat = java.text.SimpleDateFormat("dd MMMM\nyyyy", java.util.Locale.forLanguageTag("id-ID"))
        tvDateDetail.text = dateFormat.format(java.util.Date())

        // Get Intent Data
        intent?.let {
            val category = it.getStringExtra("CATEGORY")
            val source = it.getStringExtra("SOURCE")
            val amount = it.getStringExtra("AMOUNT")

            category?.let { cat -> tvCategory.text = cat }
            source?.let { src -> tvSource.text = src }
            amount?.let { amt ->
                tvAmount.text = amt
                // Adjust color based on sign
                if (amt.startsWith("-")) {
                    tvAmount.setTextColor(ContextCompat.getColor(this, R.color.expense_red))
                } else {
                    tvAmount.setTextColor(ContextCompat.getColor(this, R.color.income_green))
                }
            }
        }
    }
}
