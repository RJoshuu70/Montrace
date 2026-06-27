package com.example.utsad

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.utsad.data.AppDatabase
import com.example.utsad.data.Transaction
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TransactionFragment : Fragment() {

    private lateinit var rvTransactions: RecyclerView
    private lateinit var adapter: TransactionListAdapter
    private lateinit var database: AppDatabase
    private lateinit var tvFilterMonth: TextView
    private lateinit var btnFilterMonth: View
    private lateinit var tvNoData: TextView

    private val currentUserId = 1 // Dummy user ID matching HomeFragment
    
    private var allTransactions: List<Transaction> = emptyList()
    private var selectedMonthYear: String = SimpleDateFormat("MMMM yyyy", Locale("id", "ID")).format(Date())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_transaction, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvTransactions = view.findViewById(R.id.rv_transactions)
        tvFilterMonth = view.findViewById(R.id.tv_filter_month)
        btnFilterMonth = view.findViewById(R.id.btn_filter_month)
        tvNoData = view.findViewById(R.id.tv_no_data)
        database = AppDatabase.getDatabase(requireContext())

        setupRecyclerView()
        setupMonthFilter()
        observeTransactions()
    }

    private fun setupRecyclerView() {
        adapter = TransactionListAdapter(
            onEditClick = { transaction ->
                (activity as? MainActivity)?.editTransaction(transaction)
            },
            onDeleteClick = { transaction ->
                androidx.appcompat.app.AlertDialog.Builder(requireContext())
                    .setTitle("Delete Transaction")
                    .setMessage("Are you sure you want to delete this transaction?")
                    .setPositiveButton("Delete") { _, _ ->
                        lifecycleScope.launch {
                            database.transactionDao().deleteTransaction(transaction)
                            Toast.makeText(requireContext(), "Transaction Deleted", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
        )
        rvTransactions.layoutManager = LinearLayoutManager(requireContext())
        rvTransactions.adapter = adapter
    }
    
    private fun setupMonthFilter() {
        tvFilterMonth.text = selectedMonthYear
        btnFilterMonth.setOnClickListener {
            val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_month_year, null)
            val pickerMonth = dialogView.findViewById<android.widget.NumberPicker>(R.id.picker_month)
            val pickerYear = dialogView.findViewById<android.widget.NumberPicker>(R.id.picker_year)

            val calendar = java.util.Calendar.getInstance()
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
            pickerMonth.value = calendar.get(java.util.Calendar.MONTH)

            val currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
            pickerYear.minValue = currentYear - 10
            pickerYear.maxValue = currentYear + 10
            pickerYear.value = calendar.get(java.util.Calendar.YEAR)

            androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Pilih Bulan")
                .setView(dialogView)
                .setPositiveButton("OK") { _, _ ->
                    val selectedCalendar = java.util.Calendar.getInstance()
                    selectedCalendar.set(java.util.Calendar.YEAR, pickerYear.value)
                    selectedCalendar.set(java.util.Calendar.MONTH, pickerMonth.value)
                    val monthFormat = SimpleDateFormat("MMMM yyyy", Locale("id", "ID"))
                    selectedMonthYear = monthFormat.format(selectedCalendar.time)
                    tvFilterMonth.text = selectedMonthYear
                    filterAndDisplayTransactions()
                }
                .setNegativeButton("Batal", null)
                .show()
        }
        
        view?.findViewById<View>(R.id.btn_filter_all)?.setOnClickListener {
            selectedMonthYear = "All"
            tvFilterMonth.text = selectedMonthYear
            filterAndDisplayTransactions()
        }
    }

    private fun observeTransactions() {
        lifecycleScope.launch {
            database.transactionDao().getTransactionsByUser(currentUserId).collect { transactions ->
                allTransactions = transactions
                filterAndDisplayTransactions()
            }
        }
    }
    
    private fun filterAndDisplayTransactions() {
        val filteredList = if (selectedMonthYear == "All") {
            allTransactions
        } else {
            val monthFormat = SimpleDateFormat("MMMM yyyy", Locale("id", "ID"))
            allTransactions.filter { 
                monthFormat.format(Date(it.date)) == selectedMonthYear 
            }
        }
        
        val groupedList = mutableListOf<TransactionListItem>()
        val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
        
        var lastDate = ""
        for (transaction in filteredList) {
            val dateFormatted = dateFormat.format(Date(transaction.date))
            if (dateFormatted != lastDate) {
                groupedList.add(TransactionListItem.Header(dateFormatted))
                lastDate = dateFormatted
            }
            groupedList.add(TransactionListItem.Item(transaction))
        }
        
        if (groupedList.isEmpty()) {
            tvNoData.visibility = View.VISIBLE
            rvTransactions.visibility = View.GONE
        } else {
            tvNoData.visibility = View.GONE
            rvTransactions.visibility = View.VISIBLE
        }
        
        adapter.submitList(groupedList)
    }
}
