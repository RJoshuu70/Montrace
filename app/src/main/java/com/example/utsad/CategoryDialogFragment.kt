package com.example.utsad

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.utsad.data.Category

class CategoryDialogFragment(
    private val isExpenses: Boolean,
    private val onCategorySelected: (String) -> Unit
) : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_category_dialog, container, false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvTitle = view.findViewById<TextView>(R.id.tv_dialog_title)
        val btnClose = view.findViewById<ImageView>(R.id.btn_close_dialog)
        val rvCategories = view.findViewById<RecyclerView>(R.id.rv_categories)

        if (isExpenses) {
            tvTitle.text = "Expenses Categories"
        } else {
            tvTitle.text = "Income Categories"
        }

        btnClose.setOnClickListener {
            dismiss()
        }

        rvCategories.layoutManager = GridLayoutManager(requireContext(), 3)
        val adapter = CategoryAdapter(getCategories()) { selectedCategory ->
            onCategorySelected(selectedCategory.name)
            dismiss()
        }
        rvCategories.adapter = adapter
    }

    private fun getCategories(): List<Category> {
        return if (isExpenses) {
            listOf(
                Category("Food", R.drawable.ic_cat_food),
                Category("Public Transit", R.drawable.ic_cat_publictransit),
                Category("Shopping", R.drawable.ic_cat_shopping),
                Category("Education", R.drawable.ic_cat_education),
                Category("Fuel", R.drawable.ic_cat_fuel),
                Category("Rent", R.drawable.ic_cat_rent),
                Category("Gym", R.drawable.ic_cat_gym),
                Category("Skincare", R.drawable.ic_cat_skincare),
                Category("Telephone", R.drawable.ic_cat_telephone),
                Category("Subscription", R.drawable.ic_cat_subscription),
                Category("Bills", R.drawable.ic_cat_bills),
                Category("Parking", R.drawable.ic_cat_parking)
            )
        } else {
            listOf(
                Category("Salary", R.drawable.ic_cat_salary),
                Category("Scholarships", R.drawable.ic_cat_scholarships),
                Category("Awards", R.drawable.ic_cat_awards),
                Category("Tips", R.drawable.ic_cat_tips),
                Category("Rental", R.drawable.ic_cat_rental),
                Category("Refunds", R.drawable.ic_cat_refunds),
                Category("Royalties", R.drawable.ic_cat_royalties),
                Category("Gift", R.drawable.ic_cat_gift),
                Category("Investment", R.drawable.ic_cat_investment)
            )
        }
    }
}
