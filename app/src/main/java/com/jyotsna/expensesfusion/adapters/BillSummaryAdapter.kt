package com.jyotsna.expensesfusion.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jyotsna.expensesfusion.R
import com.jyotsna.expensesfusion.models.BillSummary

class BillSummaryAdapter(
    private val billSummaryList: List<BillSummary>
) : RecyclerView.Adapter<BillSummaryAdapter.BillSummaryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BillSummaryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_bill_summary, parent, false)
        return BillSummaryViewHolder(view)
    }

    override fun onBindViewHolder(holder: BillSummaryViewHolder, position: Int) {
        val bill = billSummaryList[position]
        holder.groupNameTextView.text = "Group: ${bill.groupName}"
        holder.billTitleTextView.text = "Title: ${bill.title}"
        holder.amountTextView.text = "Amount: $${bill.amount}"
        holder.paidByTextView.text = "Paid By: ${bill.paidBy}"
        holder.participantsTextView.text = "Participants: ${
            bill.participants.entries.joinToString { "${it.key}: ${it.value}" }
        }"
    }

    override fun getItemCount(): Int = billSummaryList.size

    class BillSummaryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val groupNameTextView: TextView = itemView.findViewById(R.id.groupNameTextView)
        val billTitleTextView: TextView = itemView.findViewById(R.id.billTitleTextView)
        val amountTextView: TextView = itemView.findViewById(R.id.amountTextView)
        val paidByTextView: TextView = itemView.findViewById(R.id.paidByTextView)
        val participantsTextView: TextView = itemView.findViewById(R.id.participantsTextView)
    }
}
