package com.jyotsna.expensesfusion.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jyotsna.expensesfusion.R


class BillsAdapter(
    private val billsList: List<Bill>
) : RecyclerView.Adapter<BillsAdapter.BillViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BillViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_bill, parent, false)
        return BillViewHolder(view)
    }

    override fun onBindViewHolder(holder: BillViewHolder, position: Int) {
        val bill = billsList[position]

        holder.titleTextView.text = bill.title
        holder.amountTextView.text = "$%.2f".format(bill.amount)

        // Display "Paid by" information
        holder.paidByTextView.text = "Paid by: ${bill.paidBy.ifEmpty { "N/A" }}"

        // Format participants map
        val participantsFormatted = bill.participants.entries.joinToString(", ") {
            "${it.key}: ${"%.2f".format(it.value)}"
        }
        holder.participantsTextView.text = "Participants: $participantsFormatted"
    }

    override fun getItemCount(): Int = billsList.size

    class BillViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        val amountTextView: TextView = itemView.findViewById(R.id.amountTextView)
        val paidByTextView: TextView = itemView.findViewById(R.id.paidByTextView)
        val participantsTextView: TextView = itemView.findViewById(R.id.participantsTextView)
    }
}
