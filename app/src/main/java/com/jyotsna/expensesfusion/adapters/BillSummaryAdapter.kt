package com.jyotsna.expensesfusion.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jyotsna.expensesfusion.R
import com.jyotsna.expensesfusion.models.BillSummary

class BillSummaryAdapter(
    private val billList: List<BillSummary>
) : RecyclerView.Adapter<BillSummaryAdapter.BillViewHolder>() {

    // ViewHolder class to hold the views
    class BillViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val groupName: TextView = itemView.findViewById(R.id.groupNameTextView)
        val title: TextView = itemView.findViewById(R.id.billTitleTextView)
        val amount: TextView = itemView.findViewById(R.id.amountTextView)
        val paidBy: TextView = itemView.findViewById(R.id.paidByTextView)
        val participants: TextView = itemView.findViewById(R.id.participantsTextView)
        val paymentStatus: TextView = itemView.findViewById(R.id.billPaymentStatusTextView) // Optional field
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BillViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_bill_summary, parent, false)
        return BillViewHolder(view)
    }

    override fun onBindViewHolder(holder: BillViewHolder, position: Int) {
        val bill = billList[position]

        // Populate the views with data
        holder.groupName.text = "Group: ${bill.groupName}"
        holder.title.text = "Title: ${bill.title}"
        holder.amount.text = "Amount: $${bill.amount}"
        holder.paidBy.text = "Paid By: ${bill.paidBy}"
        holder.participants.text = "Participants: ${
            bill.participants.entries.joinToString { "${it.key}: ${it.value}" }
        }"
        holder.paymentStatus.text = "Status: ${bill.paymentStatus}" // Optional
    }

    override fun getItemCount(): Int = billList.size
}
