package com.example.utsad

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.utsad.data.AppDatabase
import com.example.utsad.data.Transaction
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class OverviewFragment : Fragment() {

    private lateinit var database: AppDatabase
    private val currentUserId = 1

    private lateinit var tvOverviewTotal: TextView
    private lateinit var tvOverviewIncome: TextView
    private lateinit var tvOverviewExpense: TextView

    private lateinit var containerIncomeBreakdown: ViewGroup
    private lateinit var containerExpenseBreakdown: ViewGroup
    private lateinit var tvFilterMonth: TextView
    private lateinit var btnFilterMonth: View

    private var allTransactions: List<Transaction> = emptyList()
    private var selectedMonthYear: String = SimpleDateFormat("MMMM yyyy", Locale("id", "ID")).format(Date())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_overview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Inisialisasi View dari Layout XML
        tvOverviewTotal = view.findViewById(R.id.tv_overview_total)
        tvOverviewIncome = view.findViewById(R.id.tv_overview_income)
        tvOverviewExpense = view.findViewById(R.id.tv_overview_expense)

        containerIncomeBreakdown = view.findViewById(R.id.container_income_breakdown)
        containerExpenseBreakdown = view.findViewById(R.id.container_expense_breakdown)
        tvFilterMonth = view.findViewById(R.id.tv_filter_month_ov)
        btnFilterMonth = view.findViewById(R.id.btn_filter_month_ov)

        val tvDateOverview = view.findViewById<TextView>(R.id.tv_date_overview)
        val dateFormat = SimpleDateFormat("dd MMMM\nyyyy", Locale.forLanguageTag("id-ID"))
        tvDateOverview.text = dateFormat.format(Date())

        //Hubungkan ke Database
        database = AppDatabase.getDatabase(requireContext())

        setupMonthFilter()
        observeOverviewData()
    }

    private fun setupMonthFilter() {
        tvFilterMonth.text = selectedMonthYear
        btnFilterMonth.setOnClickListener {
            val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_month_year, null)
            val pickerMonth = dialogView.findViewById<android.widget.NumberPicker>(R.id.picker_month)
            val pickerYear = dialogView.findViewById<android.widget.NumberPicker>(R.id.picker_year)

            val calendar = Calendar.getInstance()
            try {
                if (selectedMonthYear != "All") {
                    val sdf = SimpleDateFormat("MMMM yyyy", Locale("id", "ID"))
                    val date = sdf.parse(selectedMonthYear)
                    if (date != null) {
                        calendar.time = date
                    }
                }
            } catch (e: Exception) {
                // ignore
            }

            val months = arrayOf("Januari", "Februari", "Maret", "April", "Mei", "Juni", "Juli", "Agustus", "September", "Oktober", "November", "Desember")
            pickerMonth.minValue = 0
            pickerMonth.maxValue = 11
            pickerMonth.displayedValues = months
            pickerMonth.value = calendar.get(Calendar.MONTH)

            val currentYear = Calendar.getInstance().get(Calendar.YEAR)
            pickerYear.minValue = currentYear - 10
            pickerYear.maxValue = currentYear + 10
            pickerYear.value = calendar.get(Calendar.YEAR)

            androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Pilih Bulan")
                .setView(dialogView)
                .setPositiveButton("OK") { _, _ ->
                    val selectedCalendar = Calendar.getInstance()
                    selectedCalendar.set(Calendar.YEAR, pickerYear.value)
                    selectedCalendar.set(Calendar.MONTH, pickerMonth.value)
                    val monthFormat = SimpleDateFormat("MMMM yyyy", Locale("id", "ID"))
                    selectedMonthYear = monthFormat.format(selectedCalendar.time)
                    tvFilterMonth.text = selectedMonthYear
                    filterAndDisplayData()
                }
                .setNegativeButton("Batal", null)
                .show()
        }
    }

    private fun observeOverviewData() {
        lifecycleScope.launch {
            database.transactionDao().getTransactionsByUser(currentUserId).collect { transactions ->
                allTransactions = transactions
                filterAndDisplayData()
            }
        }
    }

    private fun filterAndDisplayData() {
        val filteredList = if (selectedMonthYear == "All") {
            allTransactions
        } else {
            val monthFormat = SimpleDateFormat("MMMM yyyy", Locale("id", "ID"))
            allTransactions.filter { 
                monthFormat.format(Date(it.date)) == selectedMonthYear 
            }
        }

        var totalIncome = 0.0
        var totalExpense = 0.0
        val incomeByCategory = mutableMapOf<String, Double>()
        val expenseByCategory = mutableMapOf<String, Double>()

        for (transaction in filteredList) {
            if (transaction.type == "INCOME") {
                totalIncome += transaction.amount
                incomeByCategory[transaction.category] = (incomeByCategory[transaction.category] ?: 0.0) + transaction.amount
            } else if (transaction.type == "EXPENSE") {
                totalExpense += transaction.amount
                expenseByCategory[transaction.category] = (expenseByCategory[transaction.category] ?: 0.0) + transaction.amount
            }
        }

        val totalAmount = totalIncome - totalExpense

        //Format angka ke format mata uang Rupiah
        val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        tvOverviewTotal.text = format.format(totalAmount)
        tvOverviewIncome.text = format.format(totalIncome)
        tvOverviewExpense.text = format.format(totalExpense)

        // Clear containers
        containerIncomeBreakdown.removeAllViews()
        containerExpenseBreakdown.removeAllViews()

        val inflater = LayoutInflater.from(requireContext())

        // Populate Income breakdown dynamically (only used categories, sorted by amount descending)
        for ((category, amount) in incomeByCategory.entries.sortedByDescending { it.value }) {
            val pct = if (totalIncome > 0) (amount / totalIncome) * 100 else 0.0
            val itemView = inflater.inflate(R.layout.item_category_breakdown, containerIncomeBreakdown, false)

            val vCircleColor = itemView.findViewById<View>(R.id.v_circle_color)
            vCircleColor.setBackgroundResource(R.drawable.bg_circle_income)

            val tvCategoryName = itemView.findViewById<TextView>(R.id.tv_category_name)
            tvCategoryName.text = category

            val pbCategory = itemView.findViewById<ProgressBar>(R.id.pb_category)
            pbCategory.progress = pct.toInt()
            pbCategory.progressTintList = android.content.res.ColorStateList.valueOf(
                ContextCompat.getColor(requireContext(), R.color.income_green)
            )

            val tvPctCategory = itemView.findViewById<TextView>(R.id.tv_pct_category)
            tvPctCategory.text = String.format(Locale.US, "%.2f%%", pct)

            containerIncomeBreakdown.addView(itemView)
        }

        // Populate Expense breakdown dynamically (only used categories, sorted by amount descending)
        for ((category, amount) in expenseByCategory.entries.sortedByDescending { it.value }) {
            val pct = if (totalExpense > 0) (amount / totalExpense) * 100 else 0.0
            val itemView = inflater.inflate(R.layout.item_category_breakdown, containerExpenseBreakdown, false)

            val vCircleColor = itemView.findViewById<View>(R.id.v_circle_color)
            vCircleColor.setBackgroundResource(R.drawable.bg_circle_expense)

            val tvCategoryName = itemView.findViewById<TextView>(R.id.tv_category_name)
            tvCategoryName.text = category

            val pbCategory = itemView.findViewById<ProgressBar>(R.id.pb_category)
            pbCategory.progress = pct.toInt()
            pbCategory.progressTintList = android.content.res.ColorStateList.valueOf(
                ContextCompat.getColor(requireContext(), R.color.expense_red)
            )

            val tvPctCategory = itemView.findViewById<TextView>(R.id.tv_pct_category)
            tvPctCategory.text = String.format(Locale.US, "%.2f%%", pct)

            containerExpenseBreakdown.addView(itemView)
        }
    }
}
