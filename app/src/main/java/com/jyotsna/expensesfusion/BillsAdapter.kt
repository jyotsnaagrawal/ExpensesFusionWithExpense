package com.jyotsna.expensesfusion.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jyotsna.expensesfusion.R

data class Bill(
    val title: String = "",
    val amount: Double = 0.0,
    val paidBy: String = "",
    val participants: List<String> = emptyList()
)

class BillsAdapter(
    private val billsList: List<Bill>
) : RecyclerView.Adapter<BillsAdapter.BillViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BillViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_bill, parent, false)
        return BillViewHolder(view)
    }

    override fun onBindViewHolder(holder: BillViewHolder, position: Int) {
        val bill = billsList[position]
        holder.billTitleTextView.text = bill.title
        holder.billAmountTextView.text = "$${"%.2f".format(bill.amount)}"
        holder.billPaidByTextView.text = "Paid by: ${bill.paidBy}"

        // Join participants list into a readable string
        val participantsText = if (bill.participants.isNotEmpty()) {
            bill.participants.joinToString(", ")
        } else {
            "None"
        }
        holder.billParticipantsTextView.text = "Participants: $participantsText"
    }

    override fun getItemCount(): Int = billsList.size

    class BillViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val billTitleTextView: TextView = itemView.findViewById(R.id.billTitleTextView)
        val billAmountTextView: TextView = itemView.findViewById(R.id.billAmountTextView)
        val billPaidByTextView: TextView = itemView.findViewById(R.id.billPaidByTextView)
        val billParticipantsTextView: TextView = itemView.findViewById(R.id.billParticipantsTextView)
    }
}
