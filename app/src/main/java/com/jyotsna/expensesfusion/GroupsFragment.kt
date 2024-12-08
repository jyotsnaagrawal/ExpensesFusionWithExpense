package com.jyotsna.expensesfusion

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class GroupsFragment : Fragment() {

    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var groupsAdapter: GroupsAdapter
    private lateinit var fullGroupsList: List<Pair<String, Map<String, Any>>>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_groups, container, false)

        database = FirebaseDatabase.getInstance().reference
        auth = FirebaseAuth.getInstance()

        val recyclerView = view.findViewById<RecyclerView>(R.id.groupsRecyclerView)
        val searchGroupEditText = view.findViewById<EditText>(R.id.searchGroupEditText)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Initialize adapter with callbacks
        groupsAdapter = GroupsAdapter(
            requireContext(),
            onUpdateGroupClicked = ::showUpdateDialog,
            onDeleteGroupClicked = ::deleteGroup,
            onAddFriendClicked = ::showAddFriendDialog
        )
        recyclerView.adapter = groupsAdapter

        // Load groups in real-time
        loadGroups()

        // Add text watcher for the search bar
        searchGroupEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterGroups(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        return view
    }

    /**
     * Load groups from Firebase Realtime Database in real-time.
     */
    private fun loadGroups() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(requireContext(), "Please log in to view your groups", Toast.LENGTH_SHORT).show()
            return
        }

        database.child("groups")
            .orderByChild("userId")
            .equalTo(currentUser.uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    fullGroupsList = snapshot.children.map { groupSnapshot ->
                        val groupId = groupSnapshot.key ?: return@map null
                        val groupData = groupSnapshot.value as Map<String, Any>
                        Pair(groupId, groupData)
                    }.filterNotNull()

                    // Update adapter with full list
                    groupsAdapter.updateGroups(fullGroupsList)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("GroupsFragment", "Failed to load groups: ${error.message}")
                    Toast.makeText(requireContext(), "Failed to load groups.", Toast.LENGTH_SHORT).show()
                }
            })
    }

    /**
     * Filters the groups based on the search query.
     */
    private fun filterGroups(query: String) {
        val filteredGroups = if (query.isEmpty()) {
            fullGroupsList
        } else {
            fullGroupsList.filter { group ->
                val groupName = group.second["name"] as? String ?: ""
                groupName.contains(query, ignoreCase = true)
            }
        }

        groupsAdapter.updateGroups(filteredGroups)
    }

    /**
     * Show a dialog to update the group name.
     */
    private fun showUpdateDialog(groupId: String, currentName: String) {
        val editText = EditText(requireContext())
        editText.setText(currentName)

        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Update Group Name")
            .setView(editText)
            .setPositiveButton("Update") { _, _ ->
                val newName = editText.text.toString().trim()
                if (newName.isNotEmpty()) {
                    database.child("groups").child(groupId)
                        .child("name")
                        .setValue(newName)
                        .addOnSuccessListener {
                            Toast.makeText(requireContext(), "Group updated.", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { error ->
                            Log.e("GroupsFragment", "Error updating group: ${error.message}")
                            Toast.makeText(requireContext(), "Failed to update group.", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(requireContext(), "Group name cannot be empty.", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    /**
     * Deletes a group by its ID.
     */
    private fun deleteGroup(groupId: String) {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Delete Group")
            .setMessage("Are you sure you want to delete this group?")
            .setPositiveButton("Delete") { _, _ ->
                database.child("groups").child(groupId)
                    .removeValue()
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Group deleted.", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { error ->
                        Log.e("GroupsFragment", "Error deleting group: ${error.message}")
                        Toast.makeText(requireContext(), "Failed to delete group.", Toast.LENGTH_SHORT).show()
                    }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    /**
     * Show a dialog to add a friend to the group.
     */
    private fun showAddFriendDialog(groupId: String) {
        val editText = EditText(requireContext())
        editText.hint = "Enter friend's email"

        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Add Friend to Group")
            .setView(editText)
            .setPositiveButton("Add") { _, _ ->
                val email = editText.text.toString().trim()
                if (email.isNotEmpty()) {
                    database.child("groups").child(groupId)
                        .child("friends")
                        .child(email.replace(".", "_")) // Replace "." for valid Firebase keys
                        .setValue(true)
                        .addOnSuccessListener {
                            Toast.makeText(requireContext(), "Friend added successfully!", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { error ->
                            Log.e("GroupsFragment", "Error adding friend: ${error.message}")
                            Toast.makeText(requireContext(), "Failed to add friend.", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(requireContext(), "Email cannot be empty.", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
