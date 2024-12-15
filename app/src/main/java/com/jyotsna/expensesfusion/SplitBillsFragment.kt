package com.jyotsna.expensesfusion

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.jyotsna.expensesfusion.adapters.Group


class SplitBillsFragment : Fragment() {

    private lateinit var database: DatabaseReference
    private lateinit var groupsRecyclerView: RecyclerView
    private lateinit var groupsAdapter: GroupsAdapter
    private lateinit var noGroupsTextView: TextView
    private lateinit var createGroupButton: Button

    private var groupsList: MutableList<Group> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_split_bills, container, false)

        // Initialize Firebase
        database = FirebaseDatabase.getInstance().reference
        val currentUser = FirebaseAuth.getInstance().currentUser

        // Initialize Views
        noGroupsTextView = view.findViewById(R.id.noGroupsTextView)
        createGroupButton = view.findViewById(R.id.createGroupButton)
        groupsRecyclerView = view.findViewById(R.id.groupsRecyclerView)

        // RecyclerView setup
        groupsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        groupsAdapter = GroupsAdapter(groupsList) { groupId ->
            navigateToGroupDetails(groupId)
        }
        groupsRecyclerView.adapter = groupsAdapter

        // Load groups data
        loadGroupsData(currentUser?.uid)

        // Create Group Button Listener
        createGroupButton.setOnClickListener {
            showCreateGroupDialog()
        }

        return view
    }

    private fun loadGroupsData(userId: String?) {
        if (userId == null) return

        database.child("groups")
            .orderByChild("userId")
            .equalTo(userId) // Only fetch groups for the current user
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    groupsList.clear() // Clear the list before adding new data

                    snapshot.children.forEach { groupSnapshot ->
                        val groupId = groupSnapshot.key
                        val group = groupSnapshot.getValue(Group::class.java)?.copy(id = groupId ?: "")
                        group?.let {
                            groupsList.add(it) // Add each valid group to the list
                        }
                    }

                    // Log the list size for debugging
                    Log.d("SplitBillsFragment", "Groups List Size: ${groupsList.size}")

                    // Update RecyclerView
                    groupsAdapter.updateGroups(groupsList)

                    // Toggle views if the list is empty
                    toggleEmptyState(groupsList.isEmpty())
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), "Failed to load groups: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun toggleEmptyState(isEmpty: Boolean) {
        if (isEmpty) {
            groupsRecyclerView.visibility = View.GONE
            noGroupsTextView.visibility = View.VISIBLE
        } else {
            groupsRecyclerView.visibility = View.VISIBLE
            noGroupsTextView.visibility = View.GONE
        }
    }

    private fun showCreateGroupDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_create_group, null)
        val groupNameEditText = dialogView.findViewById<EditText>(R.id.groupNameEditText)

        AlertDialog.Builder(requireContext())
            .setTitle("Create Group")
            .setView(dialogView)
            .setPositiveButton("Create") { _, _ ->
                val groupName = groupNameEditText.text.toString().trim()
                if (groupName.isNotEmpty()) {
                    saveGroupToFirebase(groupName)
                } else {
                    Toast.makeText(requireContext(), "Group name cannot be empty.", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun saveGroupToFirebase(groupName: String) {
        val currentUser = FirebaseAuth.getInstance().currentUser ?: return
        val groupId = database.child("groups").push().key ?: return

        val newGroup = Group(
            id = groupId,
            name = groupName,
            userId = currentUser.uid,
            bills = null
        )

        database.child("groups").child(groupId).setValue(newGroup)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Group created successfully!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to create group.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun navigateToGroupDetails(groupId: String) {
        val fragment = GroupDetailFragment.newInstance(groupId)
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }
}
