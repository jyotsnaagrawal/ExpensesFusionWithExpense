package com.jyotsna.expensesfusion

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.jyotsna.expensesfusion.adapters.Bill
import com.jyotsna.expensesfusion.adapters.BillsAdapter

class GroupDetailFragment : Fragment() {

    private lateinit var database: DatabaseReference
    private lateinit var billsRecyclerView: RecyclerView
    private lateinit var billsAdapter: BillsAdapter
    private lateinit var groupId: String
    private val billsList: MutableList<Bill> = mutableListOf()

    companion object {
        fun newInstance(groupId: String) = GroupDetailFragment().apply {
            arguments = Bundle().apply {
                putString("groupId", groupId)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_group_details, container, false)
        groupId = arguments?.getString("groupId") ?: ""
        database = FirebaseDatabase.getInstance().reference

        billsRecyclerView = view.findViewById(R.id.billsRecyclerView)
        val addBillButton = view.findViewById<Button>(R.id.addBillButton)

        billsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        billsAdapter = BillsAdapter(billsList)
        billsRecyclerView.adapter = billsAdapter

        loadBills()

        addBillButton.setOnClickListener {
            showAddBillDialog()
        }

        return view
    }

    private fun loadBills() {
        database.child("groups").child(groupId).child("bills")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    billsList.clear()
                    snapshot.children.mapNotNullTo(billsList) {
                        it.getValue(Bill::class.java)
                    }
                    billsAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), "Failed to load bills.", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun showAddBillDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_bill, null)
        val billTitleEditText = dialogView.findViewById<EditText>(R.id.billTitleEditText)
        val amountEditText = dialogView.findViewById<EditText>(R.id.amountEditText)

        AlertDialog.Builder(requireContext())
            .setTitle("Add Bill")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val title = billTitleEditText.text.toString().trim()
                val amount = amountEditText.text.toString().toDoubleOrNull()

                if (title.isNotEmpty() && amount != null) {
                    saveBillToFirebase(title, amount)
                } else {
                    Toast.makeText(requireContext(), "Please enter valid inputs.", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun saveBillToFirebase(title: String, amount: Double) {
        val billId = database.child("groups").child(groupId).child("bills").push().key ?: return
        val newBill = Bill(title = title, amount = amount)

        database.child("groups").child(groupId).child("bills").child(billId).setValue(newBill)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Bill added successfully!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to add bill.", Toast.LENGTH_SHORT).show()
            }
    }
}
