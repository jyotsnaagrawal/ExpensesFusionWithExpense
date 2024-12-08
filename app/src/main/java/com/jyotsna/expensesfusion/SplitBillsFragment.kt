package com.jyotsna.expensesfusion

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SplitBillsFragment : Fragment() {

    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_split_bills, container, false)

        // Initialize Firebase Realtime Database
        database = FirebaseDatabase.getInstance().reference

        // Find views
        val groupNameEditText = view.findViewById<EditText>(R.id.groupNameEditText)
        val addGroupButton = view.findViewById<Button>(R.id.addGroupButton)

        // Set button click listener
        addGroupButton.setOnClickListener {
            val groupName = groupNameEditText.text.toString().trim()

            if (groupName.isNotEmpty()) {
                saveGroupToRealtimeDatabase(groupName)
                groupNameEditText.text.clear() // Clear the input after saving
            } else {
                Toast.makeText(requireContext(), "Please enter a group name", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    /**
     * Save the group to Firebase Realtime Database.
     */
    private fun saveGroupToRealtimeDatabase(groupName: String) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            Toast.makeText(requireContext(), "Please log in to save a group", Toast.LENGTH_SHORT).show()
            return
        }

        val groupId = database.child("groups").push().key // Generate a unique group ID
        if (groupId == null) {
            Toast.makeText(requireContext(), "Failed to generate group ID", Toast.LENGTH_SHORT).show()
            return
        }

        val group = mapOf(
            "name" to groupName,
            "userId" to currentUser.uid,
            "created_at" to System.currentTimeMillis(),
            "friends" to mapOf<String, Boolean>() // Empty friends list
        )

        database.child("groups").child(groupId).setValue(group)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Group added successfully!", Toast.LENGTH_SHORT).show()
                navigateToGroupsFragment()
            }
            .addOnFailureListener { exception ->
                Log.e("RealtimeDatabaseError", "Error adding group: ${exception.message}")
                Toast.makeText(requireContext(), "Failed to add group", Toast.LENGTH_SHORT).show()
            }
    }

    /**
     * Navigate to the GroupsFragment.
     */
    private fun navigateToGroupsFragment() {
        try {
            val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
            val transaction: FragmentTransaction = fragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, GroupsFragment())
            transaction.addToBackStack(null) // Optional: Add this transaction to the back stack
            transaction.commit()
            Log.d("Navigation", "Navigated to GroupsFragment")
        } catch (e: Exception) {
            Log.e("NavigationError", "Error navigating to GroupsFragment: ${e.message}")
        }
    }
}
