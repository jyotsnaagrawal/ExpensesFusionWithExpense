package com.jyotsna.expensesfusion.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jyotsna.expensesfusion.R

class BillsAdapter(
    private var billsList: List<Bill>
) : RecyclerView.Adapter<BillsAdapter.BillViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BillViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_bill, parent, false)
        return BillViewHolder(view)
    }

    override fun onBindViewHolder(holder: BillViewHolder, position: Int) {
        val bill = billsList[position]
        holder.billTitleTextView.text = bill.title
        holder.billAmountTextView.text = "$${bill.amount}"
    }

    override fun getItemCount(): Int = billsList.size

    class BillViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val billTitleTextView: TextView = itemView.findViewById(R.id.billTitleTextView)
        val billAmountTextView: TextView = itemView.findViewById(R.id.billAmountTextView)
    }
}
data class Group(
    val id: String = "",
    val name: String = "",
    val userId: String = "", // Add this field
    val bills: Map<String, Bill>? = null
)

data class Bill(
    val title: String = "",
    val amount: Double = 0.0,
    val paidBy: String = "",
    val participants: List<String> = emptyList()
)
