package com.jyotsna.expensesfusion.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jyotsna.expensesfusion.R

class ParticipantsAdapter(
    private val participants: List<String>
) : RecyclerView.Adapter<ParticipantsAdapter.ParticipantViewHolder>() {

    private val selectedParticipants = mutableSetOf<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParticipantViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_participant, parent, false)
        return ParticipantViewHolder(view)
    }

    override fun onBindViewHolder(holder: ParticipantViewHolder, position: Int) {
        val participant = participants[position]
        holder.participantNameTextView.text = participant
        holder.participantCheckBox.isChecked = selectedParticipants.contains(participant)

        holder.participantCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) selectedParticipants.add(participant)
            else selectedParticipants.remove(participant)
        }
    }

    override fun getItemCount(): Int = participants.size

    fun getSelectedParticipants(): List<String> = selectedParticipants.toList()

    class ParticipantViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val participantNameTextView: TextView = itemView.findViewById(R.id.participantTextView)
        val participantCheckBox: CheckBox = itemView.findViewById(R.id.participantCheckBox)
    }
}
