package com.example.utsad

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.Fragment

class TransactionFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_transaction, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup more buttons for all hardcoded items
        val moreButtonIds = intArrayOf(
            R.id.btn_more_1, R.id.btn_more_2, R.id.btn_more_3, R.id.btn_more_4, R.id.btn_more_5
        )

        for (id in moreButtonIds) {
            val btnMore = view.findViewById<View>(id)
            btnMore?.setOnClickListener { v -> showTransactionMenu(v) }
        }
    }

    private fun showTransactionMenu(v: View) {
        val popup = PopupMenu(requireContext(), v)
        popup.menuInflater.inflate(R.menu.menu_transaction_item, popup.menu)

        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_edit -> {
                    Toast.makeText(requireContext(), "Edit Clicked", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.menu_delete -> {
                    Toast.makeText(requireContext(), "Delete Clicked", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }

        popup.show()
    }
}
