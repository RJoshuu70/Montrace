package com.example.utsad

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.utsad.data.Transaction
import java.text.NumberFormat
import java.util.Locale

sealed class TransactionListItem {
    data class Header(val dateFormatted: String) : TransactionListItem()
    data class Item(val transaction: Transaction) : TransactionListItem()
}

class TransactionListAdapter(
    private var items: List<TransactionListItem> = emptyList(),
    private val onEditClick: ((Transaction) -> Unit)? = null,
    private val onDeleteClick: ((Transaction) -> Unit)? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_ITEM = 1
    }

    fun submitList(list: List<TransactionListItem>) {
        items = list
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is TransactionListItem.Header -> TYPE_HEADER
            is TransactionListItem.Item -> TYPE_ITEM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_HEADER -> {
                val view = inflater.inflate(R.layout.item_transaction_header, parent, false)
                HeaderViewHolder(view)
            }
            TYPE_ITEM -> {
                val view = inflater.inflate(R.layout.item_transaction, parent, false)
                ItemViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is TransactionListItem.Header -> (holder as HeaderViewHolder).bind(item.dateFormatted)
            is TransactionListItem.Item -> (holder as ItemViewHolder).bind(item.transaction)
        }
    }

    override fun getItemCount(): Int = items.size

    inner class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvHeaderDate: TextView = itemView.findViewById(R.id.tv_header_date)

        fun bind(dateFormatted: String) {
            tvHeaderDate.text = dateFormatted
        }
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTitle: TextView = itemView.findViewById(R.id.tv_trx_title)
        private val tvSource: TextView = itemView.findViewById(R.id.tv_trx_source)
        private val tvAmount: TextView = itemView.findViewById(R.id.tv_trx_amount)
        private val btnMore: ImageView = itemView.findViewById(R.id.btn_more)

        fun bind(transaction: Transaction) {
            tvTitle.text = transaction.category
            tvSource.text = transaction.source
            
            val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
            val formattedAmount = format.format(transaction.amount)
            
            if (transaction.type == "INCOME") {
                tvAmount.text = "+$formattedAmount"
                tvAmount.setTextColor(Color.parseColor("#4CAF50")) // income_green
            } else {
                tvAmount.text = "-$formattedAmount"
                tvAmount.setTextColor(Color.parseColor("#FF4D4D")) // expense_red
            }

            btnMore.setOnClickListener { anchor ->
                val popup = PopupMenu(itemView.context, anchor)
                popup.menuInflater.inflate(R.menu.menu_transaction_item, popup.menu)
                popup.setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.menu_edit -> onEditClick?.invoke(transaction)
                        R.id.menu_delete -> onDeleteClick?.invoke(transaction)
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
}
