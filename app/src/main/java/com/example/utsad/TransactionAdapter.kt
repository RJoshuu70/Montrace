package com.example.utsad

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.utsad.data.Transaction
import java.text.NumberFormat
import java.util.Locale

class TransactionAdapter(
    private var transactions: List<Transaction> = emptyList(),
    private val onEditClick: ((Transaction) -> Unit)? = null,
    private val onDeleteClick: ((Transaction) -> Unit)? = null
) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    fun submitList(list: List<Transaction>) {
        transactions = list
        notifyDataSetChanged()
    }

    inner class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCategory: TextView = itemView.findViewById(R.id.tv_category_name)
        val tvSource: TextView = itemView.findViewById(R.id.tv_source)
        val tvAmount: TextView = itemView.findViewById(R.id.tv_amount)
        val ivMore: ImageView = itemView.findViewById(R.id.iv_more_home)

        fun bind(transaction: Transaction) {
            tvCategory.text = transaction.category
            tvSource.text = transaction.source
            
            val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
            val formattedAmount = format.format(transaction.amount)
            
            if (transaction.type == "INCOME") {
                tvAmount.text = "+$formattedAmount"
                tvAmount.setTextColor(android.graphics.Color.parseColor("#4CAF50"))
            } else {
                tvAmount.text = "-$formattedAmount"
                tvAmount.setTextColor(android.graphics.Color.parseColor("#FF4D4D"))
            }

            ivMore.setOnClickListener { anchor ->
                val popup = PopupMenu(itemView.context, anchor)
                popup.menu.add(0, 1, 0, "Edit")
                popup.menu.add(0, 2, 1, "Delete")
                popup.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        1 -> onEditClick?.invoke(transaction)
                        2 -> onDeleteClick?.invoke(transaction)
                    }
                    true
                }
                popup.show()
            }

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, TransactionDetailActivity::class.java).apply {
                    putExtra("CATEGORY", transaction.category)
                    putExtra("AMOUNT", tvAmount.text.toString())
                    putExtra("SOURCE", transaction.source)
                }
                itemView.context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_transaction_home, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        holder.bind(transactions[position])
    }

    override fun getItemCount(): Int = transactions.size
}
