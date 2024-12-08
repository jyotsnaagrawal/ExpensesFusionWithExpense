package com.jyotsna.expensesfusion

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
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

        // Initialize Firebase instances
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Setup RecyclerView
        val recyclerView = view.findViewById<RecyclerView>(R.id.groupsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        groupsAdapter = GroupsAdapter()
        recyclerView.adapter = groupsAdapter

        // Load groups for the current user
        loadUserGroups()

        return view
    }

    private fun loadUserGroups() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(requireContext(), "Please log in to view your groups", Toast.LENGTH_SHORT).show()
            return
        }

        firestore.collection("groups")
            .whereEqualTo("userId", currentUser.uid) // Filter by current user
            .get()
            .addOnSuccessListener { documents ->
                val groupsList = documents.map { document ->
                    document.getString("name") ?: "Unnamed Group"
                }
                groupsAdapter.updateGroups(groupsList)
            }
            .addOnFailureListener { exception ->
                Log.e("FirestoreError", "Failed to load groups: ${exception.message}")
                Toast.makeText(requireContext(), "Failed to load groups", Toast.LENGTH_SHORT).show()
            }
    }
}
