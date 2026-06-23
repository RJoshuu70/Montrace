package com.example.utsad

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.PopupMenu
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

class HomeFragment : Fragment() {
    // True = expenses aktif, false = income aktif
    private var isExpensesActive = true

    private lateinit var btnIncome: Button
    private lateinit var btnExpenses: Button
    private lateinit var dropdownCategory: RelativeLayout
    private lateinit var tvCategoryHint: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate layout fragment_home.xml ke dalam View
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi referensi view
        btnIncome = view.findViewById(R.id.btn_income)
        btnExpenses = view.findViewById(R.id.btn_expenses)
        dropdownCategory = view.findViewById(R.id.dropdown_category)
        tvCategoryHint = view.findViewById(R.id.tv_category_hint)

        // Data dummy: tampilkan transaksi pertama (Food, Expense)
        val item1 = view.findViewById<View>(R.id.item_transaction_1)
        item1?.let {
            val tvCategory1 = it.findViewById<TextView>(R.id.tv_category_name)
            val tvAmount1 = it.findViewById<TextView>(R.id.tv_amount)
            val tvSource1 = it.findViewById<TextView>(R.id.tv_source)

            tvCategory1?.text = "Food"
            tvAmount1?.text = "-Rp20.000,00"
            tvSource1?.text = "Cash"

            val moreBtn1 = it.findViewById<View>(R.id.iv_more_home)
            moreBtn1?.setOnClickListener { v -> showTransactionMenu(v) }

            // Add intent click listener for item 1
            it.setOnClickListener {
                val intent = Intent(requireContext(), TransactionDetailActivity::class.java).apply {
                    putExtra("CATEGORY", "Food")
                    putExtra("AMOUNT", "-Rp20.000,00")
                    putExtra("SOURCE", "Cash")
                }
                startActivity(intent)
            }
        }

        // Data dummy: transaksi kedua (Transportation, Expense)
        val item2 = view.findViewById<View>(R.id.item_transaction_2)
        item2?.let {
            val tvCategory2 = it.findViewById<TextView>(R.id.tv_category_name)
            val tvAmount2 = it.findViewById<TextView>(R.id.tv_amount)
            val tvSource2 = it.findViewById<TextView>(R.id.tv_source)

            tvCategory2?.text = "Transportation"
            tvAmount2?.text = "-Rp10.000,00"
            tvSource2?.text = "E-Wallet"

            val moreBtn2 = it.findViewById<View>(R.id.iv_more_home)
            moreBtn2?.setOnClickListener { v -> showTransactionMenu(v) }

            // Add intent click listener for item 2
            it.setOnClickListener {
                val intent = Intent(requireContext(), TransactionDetailActivity::class.java).apply {
                    putExtra("CATEGORY", "Transportation")
                    putExtra("AMOUNT", "-Rp10.000,00")
                    putExtra("SOURCE", "E-Wallet")
                }
                startActivity(intent)
            }
        }

        setupToggle()
        setupDropdown()
    }

    private fun showTransactionMenu(anchor: View) {
        val popup = PopupMenu(requireContext(), anchor)
        popup.menu.add(0, 1, 0, "Edit")
        popup.menu.add(0, 2, 1, "Delete")
        popup.setOnMenuItemClickListener {
            // TODO: wire up real edit/delete logic
            true
        }
        popup.show()
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
        // Reset category hint when toggling
        tvCategoryHint.text = "Category"
        tvCategoryHint.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_hint))

        if (isExpensesActive) {
            btnExpenses.setBackgroundResource(R.drawable.bg_toggle_active)
            btnExpenses.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_white))
            btnIncome.setBackgroundResource(R.drawable.bg_toggle_inactive)
            btnIncome.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_secondary))
        } else {
            btnIncome.setBackgroundResource(R.drawable.bg_toggle_active)
            btnIncome.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_white))
            btnExpenses.setBackgroundResource(R.drawable.bg_toggle_inactive)
            btnExpenses.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_secondary))
        }
    }

    private fun setupDropdown() {
        dropdownCategory.setOnClickListener {
            val popupMenu = PopupMenu(requireContext(), dropdownCategory)
            if (isExpensesActive) {
                popupMenu.menu.add("Food")
                popupMenu.menu.add("Transportation")
                popupMenu.menu.add("Shopping")
                popupMenu.menu.add("Education")
                popupMenu.menu.add("Telephone")
            } else {
                popupMenu.menu.add("Salary")
                popupMenu.menu.add("Refunds")
                popupMenu.menu.add("Awards")
            }

            popupMenu.setOnMenuItemClickListener { item ->
                tvCategoryHint.text = item.title
                tvCategoryHint.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_primary))
                true
            }

            popupMenu.show()
        }
    }
}
