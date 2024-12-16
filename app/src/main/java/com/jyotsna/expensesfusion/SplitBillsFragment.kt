//package com.jyotsna.expensesfusion
//
//import android.app.AlertDialog
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.Button
//import android.widget.EditText
//import android.widget.Toast
//import androidx.fragment.app.Fragment
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.google.firebase.database.DatabaseReference
//import com.google.firebase.database.FirebaseDatabase
//import com.jyotsna.expensesfusion.adapters.GroupsAdapter
//import com.jyotsna.expensesfusion.models.Group
//
//class SplitBillsFragment : Fragment() {
//
//    private lateinit var database: DatabaseReference
//    private lateinit var groupsRecyclerView: RecyclerView
//    private lateinit var groupsAdapter: GroupsAdapter
//    private lateinit var createGroupButton: Button
//
//    private val groupsList = mutableListOf<Group>() // List of Group objects
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        val view = inflater.inflate(R.layout.fragment_split_bills, container, false)
//
//        // Initialize Firebase and views
//        database = FirebaseDatabase.getInstance().reference
//        createGroupButton = view.findViewById(R.id.createGroupButton)
//        groupsRecyclerView = view.findViewById(R.id.groupsRecyclerView)
//
//        // Set up RecyclerView
//        groupsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
//        groupsAdapter = GroupsAdapter(groupsList) { groupId ->
//            Toast.makeText(requireContext(), "Group Selected: $groupId", Toast.LENGTH_SHORT).show()
//        }
//        groupsRecyclerView.adapter = groupsAdapter
//
//        // Load existing groups
//        loadGroups()
//
//        // Create Group Button Click
//        createGroupButton.setOnClickListener {
//            showCreateGroupDialog()
//        }
//
//        return view
//    }
//
//    private fun showCreateGroupDialog() {
//        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_create_group, null)
//        val groupNameEditText = dialogView.findViewById<EditText>(R.id.groupNameEditText)
//
//        AlertDialog.Builder(requireContext())
//            .setTitle("Create Group")
//            .setView(dialogView)
//            .setPositiveButton("Create") { _, _ ->
//                val groupName = groupNameEditText.text.toString()
//                if (groupName.isNotEmpty()) {
//                    saveGroupToFirebase(groupName)
//                } else {
//                    Toast.makeText(requireContext(), "Group name cannot be empty", Toast.LENGTH_SHORT).show()
//                }
//            }
//            .setNegativeButton("Cancel", null)
//            .show()
//    }
//
//    private fun saveGroupToFirebase(groupName: String) {
//        val groupId = database.child("groups").push().key ?: return
//        val group = Group(id = groupId, name = groupName) // Create Group object
//
//        database.child("groups").child(groupId).setValue(group)
//            .addOnSuccessListener {
//                Toast.makeText(requireContext(), "Group created successfully!", Toast.LENGTH_SHORT).show()
//                loadGroups()
//            }
//            .addOnFailureListener {
//                Toast.makeText(requireContext(), "Failed to create group", Toast.LENGTH_SHORT).show()
//            }
//    }
//
//    private fun loadGroups() {
//        database.child("groups").get()
//            .addOnSuccessListener { snapshot ->
//                groupsList.clear()
//                snapshot.children.forEach {
//                    val group = it.getValue(Group::class.java)
//                    group?.let { groupsList.add(it) }
//                }
//                groupsAdapter.updateGroups(groupsList)
//            }
//            .addOnFailureListener {
//                Toast.makeText(requireContext(), "Failed to load groups", Toast.LENGTH_SHORT).show()
//            }
//    }
//}
