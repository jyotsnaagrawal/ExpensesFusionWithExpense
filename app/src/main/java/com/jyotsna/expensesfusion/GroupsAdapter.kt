package com.jyotsna.expensesfusion

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class GroupsAdapter(
    private val context: Context,
    private val onUpdateGroupClicked: (String, String) -> Unit,
    private val onDeleteGroupClicked: (String) -> Unit,
    private val onAddFriendClicked: (String) -> Unit
) : RecyclerView.Adapter<GroupsAdapter.GroupsViewHolder>() {

    private var groupsList: List<Pair<String, Map<String, Any>>> = emptyList()

    fun updateGroups(groups: List<Pair<String, Map<String, Any>>>) {
        groupsList = groups
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupsViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_group, parent, false)
        return GroupsViewHolder(view)
    }

    override fun onBindViewHolder(holder: GroupsViewHolder, position: Int) {
        val (groupId, groupData) = groupsList[position]
        val groupName = groupData["name"] as? String ?: "Unnamed Group"
        val friendsMap = groupData["friends"] as? Map<String, Boolean>

        val friendsList = friendsMap?.keys?.joinToString(", ") ?: "None"

        holder.groupNameTextView.text = groupName
        holder.friendsTextView.text = "Friends: $friendsList"

        holder.updateButton.setOnClickListener { onUpdateGroupClicked(groupId, groupName) }
        holder.deleteButton.setOnClickListener { onDeleteGroupClicked(groupId) }
        holder.addFriendButton.setOnClickListener { onAddFriendClicked(groupId) }
    }

    override fun getItemCount(): Int {
        return groupsList.size
    }

    class GroupsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val groupNameTextView: TextView = view.findViewById(R.id.groupNameTextView)
        val friendsTextView: TextView = view.findViewById(R.id.friendsTextView)
        val updateButton: Button = view.findViewById(R.id.updateGroupButton)
        val deleteButton: Button = view.findViewById(R.id.deleteGroupButton)
        val addFriendButton: Button = view.findViewById(R.id.addFriendButton)
    }
}
