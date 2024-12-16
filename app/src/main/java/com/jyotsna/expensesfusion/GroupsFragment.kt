package com.jyotsna.expensesfusion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.jyotsna.expensesfusion.adapters.GroupsAdapter
import com.jyotsna.expensesfusion.models.Group

class GroupsFragment : Fragment() {

    private lateinit var database: DatabaseReference
    private lateinit var groupsRecyclerView: RecyclerView
    private lateinit var groupsAdapter: GroupsAdapter
    private lateinit var createGroupButton: Button

    private val groupsList = mutableListOf<Group>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_groups, container, false)

        database = FirebaseDatabase.getInstance().reference
        createGroupButton = view.findViewById(R.id.createGroupButton)
        groupsRecyclerView = view.findViewById(R.id.groupsRecyclerView)

        groupsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        groupsAdapter = GroupsAdapter(groupsList) { groupId ->
            navigateToGroupDetails(groupId)
        }
        groupsRecyclerView.adapter = groupsAdapter

        createGroupButton.setOnClickListener {
            showCreateGroupDialog()
        }

        loadGroups()

        return view
    }

    private fun showCreateGroupDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_create_group, null)
        val groupNameEditText = dialogView.findViewById<android.widget.EditText>(R.id.groupNameEditText)

        android.app.AlertDialog.Builder(requireContext())
            .setTitle("Create Group")
            .setView(dialogView)
            .setPositiveButton("Create") { _, _ ->
                val groupName = groupNameEditText.text.toString()
                if (groupName.isNotEmpty()) {
                    saveGroupToFirebase(groupName)
                } else {
                    Toast.makeText(requireContext(), "Group name cannot be empty", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun saveGroupToFirebase(groupName: String) {
        val groupId = database.child("groups").push().key ?: return
        val group = Group(groupId, groupName)
        database.child("groups").child(groupId).setValue(group)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Group created successfully!", Toast.LENGTH_SHORT).show()
                loadGroups()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to create group", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadGroups() {
        database.child("groups").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                groupsList.clear()
                snapshot.children.forEach { groupSnapshot ->
                    val group = groupSnapshot.getValue(Group::class.java)
                    group?.let { groupsList.add(it) }
                }
                groupsAdapter.updateGroups(groupsList)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Failed to load groups", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Correct navigation to GroupDetailFragment
    private fun navigateToGroupDetails(groupId: String) {
        val groupDetailFragment = GroupDetailFragment.newInstance(groupId)
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, groupDetailFragment)
            .addToBackStack(null) // Allows back navigation
            .commit()
    }
}
