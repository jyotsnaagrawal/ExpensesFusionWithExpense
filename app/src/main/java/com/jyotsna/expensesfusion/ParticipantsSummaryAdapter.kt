package com.jyotsna.expensesfusion.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jyotsna.expensesfusion.R

class ParticipantsSummaryAdapter(
    private var participantsList: List<Pair<String, Double>>
) : RecyclerView.Adapter<ParticipantsSummaryAdapter.ParticipantViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParticipantViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_participant_summary, parent, false)
        return ParticipantViewHolder(view)
    }

    override fun onBindViewHolder(holder: ParticipantViewHolder, position: Int) {
        val (name, amount) = participantsList[position]
        holder.participantNameTextView.text = name
        holder.amountTextView.text = "$%.2f".format(amount)
    }

    override fun getItemCount(): Int = participantsList.size

    fun updateData(newList: List<Pair<String, Double>>) {
        participantsList = newList
        notifyDataSetChanged()
    }

    class ParticipantViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val participantNameTextView: TextView = itemView.findViewById(R.id.participantNameTextView)
        val amountTextView: TextView = itemView.findViewById(R.id.amountTextView)
    }
}
