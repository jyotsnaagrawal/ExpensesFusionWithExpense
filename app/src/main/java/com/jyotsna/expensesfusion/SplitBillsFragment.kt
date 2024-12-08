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
        val group = hashMapOf(
            "name" to groupName,
            "created_at" to System.currentTimeMillis()
        )

        firestore.collection("groups")
            .add(group)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Group added successfully!", Toast.LENGTH_SHORT)
                    .show()
            }
            .addOnFailureListener { exception ->
                Log.e("FirestoreError", "Error adding group: ${exception.message}")
                Toast.makeText(
                    requireContext(),
                    "Failed to add group: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

}