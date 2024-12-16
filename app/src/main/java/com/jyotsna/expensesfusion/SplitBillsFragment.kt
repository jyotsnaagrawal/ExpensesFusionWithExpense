package com.jyotsna.expensesfusion

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.jyotsna.expensesfusion.adapters.Bill
import com.jyotsna.expensesfusion.adapters.BillsAdapter
import com.jyotsna.expensesfusion.adapters.ParticipantsAdapter

class SplitBillsFragment : Fragment() {

    private lateinit var database: DatabaseReference
    private lateinit var youOweTextView: TextView
    private lateinit var youAreOwedTextView: TextView
    private lateinit var billsRecyclerView: RecyclerView
    private lateinit var billsAdapter: BillsAdapter
    private val billsList = mutableListOf<Bill>()
    private val groupMembers = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_split_bills, container, false)

        database = FirebaseDatabase.getInstance().reference
        val currentUser = FirebaseAuth.getInstance().currentUser ?: return view

        // Initialize UI
        youOweTextView = view.findViewById(R.id.youOweAmountTextView)
        youAreOwedTextView = view.findViewById(R.id.youAreOwedAmountTextView)
        billsRecyclerView = view.findViewById(R.id.billsRecyclerView)
        val addBillButton = view.findViewById<Button>(R.id.addBillButton)

        billsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        billsAdapter = BillsAdapter(billsList)
        billsRecyclerView.adapter = billsAdapter

        loadGroupMembers("group1") // Example groupId
        loadBillsData("group1", currentUser.uid)

        addBillButton.setOnClickListener {
            showAddBillDialog("group1")
        }

        return view
    }

    private fun loadGroupMembers(groupId: String) {
        database.child("groups").child(groupId).child("members")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    groupMembers.clear()
                    snapshot.children.mapNotNullTo(groupMembers) { it.getValue(String::class.java) }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), "Failed to load group members.", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun loadBillsData(groupId: String, userId: String) {
        database.child("groups").child(groupId).child("bills")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    billsList.clear()
                    var totalYouOwe = 0.0
                    var totalYouAreOwed = 0.0

                    snapshot.children.forEach { billSnapshot ->
                        val bill = billSnapshot.getValue(Bill::class.java)
                        bill?.let {
                            if (it.paidBy == userId) {
                                totalYouAreOwed += it.amount ?: 0.0
                            } else if (it.participants.contains(userId)) {
                                totalYouOwe += (it.amount ?: 0.0) / it.participants.size
                            }
                            billsList.add(it)
                        }
                    }

                    billsAdapter.notifyDataSetChanged()
                    youOweTextView.text = "$%.2f".format(totalYouOwe)
                    youAreOwedTextView.text = "$%.2f".format(totalYouAreOwed)
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), "Failed to load bills.", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun showAddBillDialog(groupId: String) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_bill, null)
        val titleEditText = dialogView.findViewById<EditText>(R.id.billTitleEditText)
        val amountEditText = dialogView.findViewById<EditText>(R.id.amountEditText)
        val paidBySpinner = dialogView.findViewById<Spinner>(R.id.paidBySpinner)
        val participantsRecyclerView = dialogView.findViewById<RecyclerView>(R.id.participantsRecyclerView)
        val addParticipantButton = dialogView.findViewById<Button>(R.id.addParticipantButton)

        val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, groupMembers)
        paidBySpinner.adapter = spinnerAdapter

        val participantsAdapter = ParticipantsAdapter(groupMembers)
        participantsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        participantsRecyclerView.adapter = participantsAdapter

        addParticipantButton.setOnClickListener {
            showAddParticipantDialog { newParticipant ->
                groupMembers.add(newParticipant)
                spinnerAdapter.notifyDataSetChanged()
                participantsAdapter.notifyDataSetChanged()
            }
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Add Bill")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val title = titleEditText.text.toString()
                val amount = amountEditText.text.toString().toDoubleOrNull()
                val paidBy = paidBySpinner.selectedItem.toString()
                val selectedParticipants = participantsAdapter.getSelectedParticipants()

                if (title.isNotEmpty() && amount != null && selectedParticipants.isNotEmpty()) {
                    saveBillToFirebase(groupId, title, amount, paidBy, selectedParticipants)
                } else {
                    Toast.makeText(requireContext(), "Please fill all fields correctly.", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showAddParticipantDialog(onParticipantAdded: (String) -> Unit) {
        val inputEditText = EditText(requireContext())
        inputEditText.hint = "Enter Participant Email"

        AlertDialog.Builder(requireContext())
            .setTitle("Add Participant")
            .setView(inputEditText)
            .setPositiveButton("Add") { _, _ ->
                val newParticipant = inputEditText.text.toString().trim()
                if (newParticipant.isNotEmpty()) {
                    onParticipantAdded(newParticipant)
                } else {
                    Toast.makeText(requireContext(), "Participant email cannot be empty.", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun saveBillToFirebase(groupId: String, title: String, amount: Double, paidBy: String, participants: List<String>) {
        val billId = database.child("groups").child(groupId).child("bills").push().key ?: return
        val bill = Bill(title, amount, paidBy, participants)

        database.child("groups").child(groupId).child("bills").child(billId).setValue(bill)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Bill added successfully!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to add bill.", Toast.LENGTH_SHORT).show()
            }
    }
}
