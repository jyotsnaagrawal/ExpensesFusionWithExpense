package com.jyotsna.expensesfusion

import android.app.AlertDialog
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
import com.jyotsna.expensesfusion.adapters.Group

class GroupsFragment : Fragment() {

    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var groupsAdapter: GroupsAdapter
    private var fullGroupsList: List<Group> = listOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_groups, container, false)

        database = FirebaseDatabase.getInstance().reference
        auth = FirebaseAuth.getInstance()

        val recyclerView = view.findViewById<RecyclerView>(R.id.groupsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Initialize adapter with group click listener
        groupsAdapter = GroupsAdapter(fullGroupsList) { groupId ->
            navigateToGroupDetails(groupId)
        }
        recyclerView.adapter = groupsAdapter

        loadGroups()
        return view
    }

    private fun loadGroups() {
        val currentUser = auth.currentUser ?: return

        database.child("groups")
            .orderByChild("userId")
            .equalTo(currentUser.uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val groupList = snapshot.children.mapNotNull {
                        it.getValue(Group::class.java)?.copy(id = it.key ?: "")
                    }
                    fullGroupsList = groupList
                    groupsAdapter.updateGroups(fullGroupsList)
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), "Failed to load groups.", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun navigateToGroupDetails(groupId: String) {
        val fragment = GroupDetailFragment.newInstance(groupId)
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }
}
