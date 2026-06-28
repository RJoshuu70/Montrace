package com.example.utsad

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.utsad.data.AppDatabase
import com.example.utsad.data.Transaction
import com.example.utsad.data.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeFragment : Fragment() {
    private var isExpensesActive = true

    private lateinit var btnIncome: Button
    private lateinit var btnExpenses: Button
    private lateinit var dropdownCategory: RelativeLayout
    private lateinit var tvCategoryHint: TextView
    private lateinit var etAmount: EditText
    private lateinit var etSource: EditText
    private lateinit var btnSaveHome: Button
    private lateinit var tvDateHome: TextView
    private lateinit var tvTotalAmount: TextView
    private lateinit var rvRecentTransactions: RecyclerView

    private lateinit var transactionAdapter: TransactionAdapter
    private lateinit var database: AppDatabase

    private val currentUserId = 1 // Dummy User ID

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Init views
        btnIncome = view.findViewById(R.id.btn_income)
        btnExpenses = view.findViewById(R.id.btn_expenses)
        dropdownCategory = view.findViewById(R.id.dropdown_category)
        tvCategoryHint = view.findViewById(R.id.tv_category_hint)
        etAmount = view.findViewById(R.id.et_amount)
        etSource = view.findViewById(R.id.et_source)
        btnSaveHome = view.findViewById(R.id.btn_save_home)
        tvDateHome = view.findViewById(R.id.tv_date_home)
        tvTotalAmount = view.findViewById(R.id.tv_total_amount)
        rvRecentTransactions = view.findViewById(R.id.rv_recent_transactions)

        val tvViewAll = view.findViewById<TextView>(R.id.tv_view_all)
        tvViewAll.setOnClickListener {
            (activity as? MainActivity)?.navigateToTransaction()
        }

        database = AppDatabase.getDatabase(requireContext())

        setupHeader()
        setupToggle()
        setupDropdown()
        setupRecyclerView()
        setupSaveButton()

        // Init dummy user and observe database
        lifecycleScope.launch {
            initDummyUser()
            observeTransactions()
            observeTotalAmount()
        }
    }

    private fun setupHeader() {
        val dateFormat = SimpleDateFormat("dd MMMM\nyyyy", Locale("id", "ID"))
        tvDateHome.text = dateFormat.format(Date())
    }

    private fun setupToggle() {
        btnIncome.setOnClickListener {
            if (isExpensesActive) {
                isExpensesActive = false
                updateToggleUI()
            }
        }

        btnExpenses.setOnClickListener {
            if (!isExpensesActive) {
                isExpensesActive = true
                updateToggleUI()
            }
        }
    }

    private fun updateToggleUI() {
        tvCategoryHint.text = "Category"
        tvCategoryHint.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_hint))

        if (isExpensesActive) {
            btnExpenses.setBackgroundResource(R.drawable.bg_toggle_active)
            btnExpenses.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_white))
            btnIncome.setBackgroundResource(R.drawable.bg_toggle_inactive)
            btnIncome.setTextColor(ContextCompat.getColor(requireContext(), R.color.sys_navy_900))
        } else {
            btnIncome.setBackgroundResource(R.drawable.bg_toggle_active)
            btnIncome.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_white))
            btnExpenses.setBackgroundResource(R.drawable.bg_toggle_inactive)
            btnExpenses.setTextColor(ContextCompat.getColor(requireContext(), R.color.sys_navy_900))
        }
    }

    private fun setupDropdown() {
        dropdownCategory.setOnClickListener {
            val dialog = CategoryDialogFragment(isExpensesActive) { selectedCategory ->
                tvCategoryHint.text = selectedCategory
                tvCategoryHint.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_primary))
            }
            dialog.show(childFragmentManager, "CategoryDialog")
        }
    }

    private fun setupRecyclerView() {
        transactionAdapter = TransactionAdapter(
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
                            android.widget.Toast.makeText(requireContext(), "Transaction Deleted", android.widget.Toast.LENGTH_SHORT).show()
                        }
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
        )
        rvRecentTransactions.layoutManager = LinearLayoutManager(requireContext())
        rvRecentTransactions.adapter = transactionAdapter
    }

    private fun setupSaveButton() {
        btnSaveHome.setOnClickListener {
            val category = tvCategoryHint.text.toString()
            val amountStr = etAmount.text.toString()
            val source = etSource.text.toString()

            if (category == "Category" || amountStr.isEmpty() || source.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val amount = amountStr.toDoubleOrNull()
            if (amount == null) {
                Toast.makeText(requireContext(), "Invalid amount", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val type = if (isExpensesActive) "EXPENSE" else "INCOME"
            
            if (editingTransaction != null) {
                // Update data
                val updatedTransaction = editingTransaction!!.copy(
                    type = type,
                    category = category,
                    amount = amount,
                    source = source
                )
                lifecycleScope.launch(Dispatchers.IO) {
                    database.transactionDao().updateTransaction(updatedTransaction)
                    withContext(Dispatchers.Main) {
                        resetInputFields()
                        Toast.makeText(requireContext(), "Transaction Updated!", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                // Insert new data
                val transaction = Transaction(
                    user_id = currentUserId,
                    type = type,
                    category = category,
                    amount = amount,
                    source = source,
                    date = System.currentTimeMillis()
                )

                lifecycleScope.launch(Dispatchers.IO) {
                    database.transactionDao().insertTransaction(transaction)
                    withContext(Dispatchers.Main) {
                        resetInputFields()
                        Toast.makeText(requireContext(), "Transaction Saved!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun resetInputFields() {
        editingTransaction = null
        tvCategoryHint.text = "Category"
        tvCategoryHint.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_hint))
        etAmount.text.clear()
        etSource.text.clear()
        btnSaveHome.text = "Save"
    }

    private var editingTransaction: Transaction? = null

    fun setEditTransaction(transaction: Transaction) {
        editingTransaction = transaction
        
        // Load transaction data
        isExpensesActive = transaction.type == "EXPENSE"
        updateToggleUI()
        
        tvCategoryHint.text = transaction.category
        tvCategoryHint.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_primary))
        
        val amountStr = if (transaction.amount % 1 == 0.0) {
            transaction.amount.toInt().toString()
        } else {
            transaction.amount.toString()
        }
        etAmount.setText(amountStr)
        etSource.setText(transaction.source)
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (hidden) {
            resetInputFields()
        }
    }

    private suspend fun initDummyUser() {
        withContext(Dispatchers.IO) {
            val dummyEmail = "dummy@montrace.com"
            val user = database.userDao().getUserByEmail(dummyEmail)
            if (user == null) {
                database.userDao().insertUser(User(id = currentUserId, name = "Dummy User", email = dummyEmail, password = "password"))
            }
        }
    }

    private fun observeTransactions() {
        lifecycleScope.launch {
            database.transactionDao().getTransactionsByUser(currentUserId).collect { list ->
                transactionAdapter.submitList(list.take(3))
            }
        }
    }

    private fun observeTotalAmount() {
        lifecycleScope.launch {
            val incomeFlow = database.transactionDao().getTotalIncomeByUser(currentUserId)
            val expenseFlow = database.transactionDao().getTotalExpenseByUser(currentUserId)
            
            incomeFlow.combine(expenseFlow) { income, expense ->
                val totalIncome = income ?: 0.0
                val totalExpense = expense ?: 0.0
                totalIncome - totalExpense
            }.collect { total ->
                val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
                tvTotalAmount.text = format.format(total)
            }
        }
    }
}
