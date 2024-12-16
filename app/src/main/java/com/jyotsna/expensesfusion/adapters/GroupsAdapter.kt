package com.jyotsna.expensesfusion.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jyotsna.expensesfusion.R
import com.jyotsna.expensesfusion.models.Group

class GroupsAdapter(
    private var groupsList: List<Group>,
    private val onGroupSelected: (String) -> Unit // Callback for group selection
) : RecyclerView.Adapter<GroupsAdapter.GroupViewHolder>() {

    class GroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val groupNameTextView: TextView = itemView.findViewById(R.id.groupNameTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_group, parent, false)
        return GroupViewHolder(view)
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        val group = groupsList[position]
        holder.groupNameTextView.text = group.name
        // Access the Group's name

        holder.itemView.setOnClickListener {
            onGroupSelected(group.id) // Pass group ID on selection
        }
    }

    override fun getItemCount(): Int = groupsList.size

    fun updateGroups(newGroups: List<Group>) {
        groupsList = newGroups
        notifyDataSetChanged()
    }
}
