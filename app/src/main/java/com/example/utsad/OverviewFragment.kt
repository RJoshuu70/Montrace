package com.example.utsad

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.utsad.data.AppDatabase
import com.example.utsad.data.Transaction
import kotlinx.coroutines.launch
import org.w3c.dom.Text
import java.text.NumberFormat
import java.util.Locale

class OverviewFragment : Fragment() {

    private lateinit var database: AppDatabase
    private val currentUserId = 1

    private lateinit var tvOverviewTotal: TextView
    private lateinit var tvOverviewIncome: TextView
    private lateinit var tvOverviewExpense: TextView

    private lateinit var progressAwards: ProgressBar
    private lateinit var tvPctAwards: TextView
    private lateinit var progressSalary: ProgressBar
    private lateinit var tvPctSalary: TextView

    private lateinit var progressFood: ProgressBar
    private lateinit var tvPctFood: TextView
    private lateinit var progressTransport: ProgressBar
    private lateinit var tvPctTransport: TextView

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

        progressAwards = view.findViewById(R.id.progress_awards)
        tvPctAwards = view.findViewById(R.id.tv_pct_awards)
        progressSalary = view.findViewById(R.id.progress_salary)
        tvPctSalary = view.findViewById(R.id.tv_pct_salary)

        progressFood = view.findViewById(R.id.progress_food)
        tvPctFood = view.findViewById(R.id.tv_pct_food)
        progressTransport = view.findViewById(R.id.progress_transport)
        tvPctTransport = view.findViewById(R.id.tv_pct_transport)

        //Hubungkan ke Database
        database = AppDatabase.getDatabase(requireContext())

        observeOverviewData()
    }

    private fun observeOverviewData() {
        lifecycleScope.launch {
            database.transactionDao().getTransactionsByUser(currentUserId).collect {
                transactions ->
                calculateAndPopulateUI(transactions)
            }
        }
    }

    private fun calculateAndPopulateUI(transactions: List<Transaction>) {
        var totalIncome = 0.0
        var totalExpense = 0.0

        var incomeAwards = 0.0
        var incomeSalary = 0.0

        var expenseFood = 0.0
        var expenseTransport = 0.0

        for (transaction in transactions) {
            if (transaction.type == "INCOME") {
                totalIncome += transaction.amount
                when (transaction.category) {
                    "Awards" -> incomeAwards += transaction.amount
                    "Salary" -> incomeSalary += transaction.amount
                }
            } else if (transaction.type == "EXPENSE") {
                totalExpense += transaction.amount
                when (transaction.category) {
                    "Food" -> expenseFood += transaction.amount
                    // Menoleransi input kategori "Public Transit" dari dialog kategori maupun "Transportation"
                    "Public Transit", "Transportation" -> expenseTransport += transaction.amount
                }
            }
        }

        val totalAmount = totalIncome - totalExpense

        //Format angka ke format mata uang Rupiah
        val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        tvOverviewTotal.text = format.format(totalAmount)
        tvOverviewIncome.text = format.format(totalIncome)
        tvOverviewExpense.text = format.format(totalExpense)

        //Hitung persentase Kategori Pemasukan (Income)
        val awardsPct = if (totalIncome > 0) (incomeAwards / totalIncome) * 100 else 0.0
        var salaryPct = if (totalIncome > 0) (incomeSalary / totalIncome) * 100 else 0.0

        //Hitung persentase Kategori Pengeluaran (Expense)
        val foodPct = if (totalExpense > 0) (expenseFood / totalExpense) * 100 else 0.0
        val transportPct = if (totalExpense > 0) (expenseTransport / totalExpense) * 100 else 0.0

        //Set Nilai Progress Bar
        progressAwards.progress = awardsPct.toInt()
        progressSalary.progress = salaryPct.toInt()
        progressFood.progress = foodPct.toInt()
        progressTransport.progress = transportPct.toInt()

        //Tampilkan Teks Persentase dengan format 2 angka di belakang koma
        tvPctAwards.text = String.format(Locale.US, "%.2f%%", awardsPct)
        tvPctSalary.text = String.format(Locale.US, "%.2f%%", salaryPct)
        tvPctFood.text = String.format(Locale.US, "%.2f%%", foodPct)
        tvPctTransport.text = String.format(Locale.US, "%.2f%%", transportPct)
    }
}
