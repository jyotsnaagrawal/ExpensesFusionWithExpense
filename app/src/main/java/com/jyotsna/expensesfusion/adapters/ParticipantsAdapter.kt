package com.jyotsna.expensesfusion.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import com.jyotsna.expensesfusion.R

data class ParticipantSelection(
    var name: String = "",
    var isPaid: Boolean = false,
    var isOwes: Boolean = false
)

class ParticipantsAdapter(
    private val participants: MutableList<ParticipantSelection>
) : RecyclerView.Adapter<ParticipantsAdapter.ParticipantViewHolder>() {

    fun addParticipant(name: String) {
        participants.add(ParticipantSelection(name))
        notifyItemInserted(participants.size - 1)
    }

    fun getParticipants(): List<ParticipantSelection> = participants

    class ParticipantViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val participantName: EditText = itemView.findViewById(R.id.participantEditText)
        val checkBoxPaid: CheckBox = itemView.findViewById(R.id.checkBoxPaid)
        val checkBoxOwes: CheckBox = itemView.findViewById(R.id.checkBoxOwes)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParticipantViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_participant, parent, false)
        return ParticipantViewHolder(view)
    }

    override fun onBindViewHolder(holder: ParticipantViewHolder, position: Int) {
        val participant = participants[position]

        holder.participantName.setText(participant.name)
        holder.checkBoxPaid.isChecked = participant.isPaid
        holder.checkBoxOwes.isChecked = participant.isOwes

        holder.participantName.setOnFocusChangeListener { _, _ ->
            participant.name = holder.participantName.text.toString()
        }

        holder.checkBoxPaid.setOnCheckedChangeListener { _, isChecked ->
            participant.isPaid = isChecked
            if (isChecked) {
                participants.forEachIndexed { index, p ->
                    if (index != position) p.isPaid = false
                }
                notifyDataSetChanged()
            }
        }

        holder.checkBoxOwes.setOnCheckedChangeListener { _, isChecked ->
            participant.isOwes = isChecked
        }
    }

    override fun getItemCount(): Int = participants.size
}
