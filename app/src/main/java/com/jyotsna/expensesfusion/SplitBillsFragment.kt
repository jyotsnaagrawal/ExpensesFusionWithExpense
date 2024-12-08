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
import com.google.firebase.firestore.FirebaseFirestore

class SplitBillsFragment : Fragment() {

    private lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_split_bills, container, false)

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance()

        // Find views
        val groupNameEditText = view.findViewById<EditText>(R.id.groupNameEditText)
        val addGroupButton = view.findViewById<Button>(R.id.addGroupButton)

        // Set button click listener
        addGroupButton.setOnClickListener {
            val groupName = groupNameEditText.text.toString().trim()

            if (groupName.isNotEmpty()) {
                saveGroupToFirestore(groupName)
                groupNameEditText.text.clear() // Clear the input after saving
            } else {
                Toast.makeText(requireContext(), "Please enter a group name", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        return view
    }

    private fun saveGroupToFirestore(groupName: String) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            Toast.makeText(requireContext(), "Please log in to save a group", Toast.LENGTH_SHORT)
                .show()
            return
        }

        val group = hashMapOf(
            "name" to groupName,
            "userId" to currentUser.uid,
            "created_at" to System.currentTimeMillis()
        )

        firestore.collection("groups")
            .add(group)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Group added successfully!", Toast.LENGTH_SHORT)
                    .show()

                // Navigate to GroupsFragment
                navigateToGroupsFragment()
            }
            .addOnFailureListener { exception ->
                Log.e("FirestoreError", "Error adding group: ${exception.message}")
                Toast.makeText(requireContext(), "Failed to add group", Toast.LENGTH_SHORT).show()
            }
    }

    private fun navigateToGroupsFragment() {
        val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
        val transaction: FragmentTransaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, GroupsFragment())
        transaction.addToBackStack(null) // Optional: Add this transaction to the back stack
        transaction.commit()
    }
}
