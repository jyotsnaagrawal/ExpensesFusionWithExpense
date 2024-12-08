package com.jyotsna.expensesfusion

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class GroupsAdapter : RecyclerView.Adapter<GroupsAdapter.GroupsViewHolder>() {

    private var groupsList: List<String> = emptyList()

    fun updateGroups(groups: List<String>) {
        groupsList = groups
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_group, parent, false)
        return GroupsViewHolder(view)
    }

    override fun onBindViewHolder(holder: GroupsViewHolder, position: Int) {
        holder.groupNameTextView.text = groupsList[position]
    }

    override fun getItemCount(): Int {
        return groupsList.size
    }

    class GroupsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val groupNameTextView: TextView = view.findViewById(R.id.groupNameTextView)
    }
}
