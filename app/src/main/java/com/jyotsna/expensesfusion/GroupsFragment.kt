package com.jyotsna.expensesfusion

import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class GroupsFragment : Fragment() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var groupsAdapter: GroupsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_groups, container, false)

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        val recyclerView = view.findViewById<RecyclerView>(R.id.groupsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Initialize the adapter with callbacks
        groupsAdapter = GroupsAdapter(
            requireContext(),
            onUpdateGroupClicked = ::showUpdateDialog,
            onDeleteGroupClicked = ::deleteGroup,
            onAddFriendClicked = ::showAddFriendDialog
        )
        recyclerView.adapter = groupsAdapter

        // Load groups in real-time
        loadGroups()

        return view
    }

    /**
     * Loads groups in real-time using Firestore snapshot listener.
     */
    private fun loadGroups() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(requireContext(), "Please log in to view your groups", Toast.LENGTH_SHORT).show()
            return
        }

        firestore.collection("groups")
            .whereEqualTo("userId", currentUser.uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("GroupsFragment", "Error loading groups: ${error.message}")
                    Toast.makeText(requireContext(), "Failed to load groups.", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    val groupsList = snapshot.documents.map { document ->
                        Pair(document.id, document.data ?: emptyMap())
                    }
                    groupsAdapter.updateGroups(groupsList)
                }
            }
    }

    /**
     * Shows a dialog to update the group's name.
     */
    private fun showUpdateDialog(groupId: String, currentName: String) {
        val editText = EditText(requireContext())
        editText.setText(currentName)
        editText.inputType = InputType.TYPE_CLASS_TEXT

        AlertDialog.Builder(requireContext())
            .setTitle("Update Group Name")
            .setView(editText)
            .setPositiveButton("Update") { _, _ ->
                val newName = editText.text.toString().trim()
                if (newName.isNotEmpty()) {
                    firestore.collection("groups").document(groupId)
                        .update("name", newName)
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
     * Deletes a group based on its ID.
     */
    private fun deleteGroup(groupId: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Group")
            .setMessage("Are you sure you want to delete this group?")
            .setPositiveButton("Delete") { _, _ ->
                firestore.collection("groups").document(groupId)
                    .delete()
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
     * Shows a dialog to add a friend to the group.
     */
    private fun showAddFriendDialog(groupId: String) {
        val editText = EditText(requireContext())
        editText.hint = "Enter friend's email"
        editText.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS

        AlertDialog.Builder(requireContext())
            .setTitle("Add Friend to Group")
            .setView(editText)
            .setPositiveButton("Add") { _, _ ->
                val email = editText.text.toString().trim()
                if (email.isNotEmpty()) {
                    firestore.collection("groups").document(groupId)
                        .update("friends", FieldValue.arrayUnion(email))
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
