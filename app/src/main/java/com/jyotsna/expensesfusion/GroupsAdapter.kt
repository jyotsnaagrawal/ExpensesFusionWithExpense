package com.jyotsna.expensesfusion

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class GroupsAdapter(
    private val context: Context,
    private val onUpdateGroupClicked: (String, String) -> Unit,
    private val onDeleteGroupClicked: (String) -> Unit,
    private val onAddFriendClicked: (String) -> Unit // Callback for adding a friend
) : RecyclerView.Adapter<GroupsAdapter.GroupsViewHolder>() {

    private var groupsList: List<Pair<String, Map<String, Any>>> = emptyList()

    fun updateGroups(groups: List<Pair<String, Map<String, Any>>>) {
        groupsList = groups
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_group, parent, false)
        return GroupsViewHolder(view)
    }

    override fun onBindViewHolder(holder: GroupsViewHolder, position: Int) {
        val (groupId, groupData) = groupsList[position]
        val groupName = groupData["name"] as? String ?: "Unnamed Group"
        val friends = groupData["friends"] as? List<String> ?: emptyList()

        holder.groupNameTextView.text = groupName
        holder.friendsListTextView.text = if (friends.isNotEmpty()) {
            "Friends: ${friends.joinToString(", ")}"
        } else {
            "Friends: None"
        }

        // Update Button
        holder.updateGroupButton.setOnClickListener {
            onUpdateGroupClicked(groupId, groupName)
        }

        // Delete Button
        holder.deleteGroupButton.setOnClickListener {
            onDeleteGroupClicked(groupId)
        }

        // Add Friend Button
        holder.addFriendButton.setOnClickListener {
            onAddFriendClicked(groupId)
        }
    }

    override fun getItemCount(): Int {
        return groupsList.size
    }

    class GroupsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val groupNameTextView: TextView = view.findViewById(R.id.groupNameTextView)
        val friendsListTextView: TextView = view.findViewById(R.id.friendsListTextView)
        val updateGroupButton: Button = view.findViewById(R.id.updateGroupButton)
        val deleteGroupButton: Button = view.findViewById(R.id.deleteGroupButton)
        val addFriendButton: Button = view.findViewById(R.id.addFriendButton) // Add Friend button binding
    }
}
