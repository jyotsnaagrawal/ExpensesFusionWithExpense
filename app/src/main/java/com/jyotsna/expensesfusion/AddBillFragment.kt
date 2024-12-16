package com.jyotsna.expensesfusion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import com.jyotsna.expensesfusion.adapters.ParticipantSelection
import com.jyotsna.expensesfusion.adapters.ParticipantsAdapter

class AddBillFragment : Fragment() {

    private lateinit var groupId: String
    private lateinit var billTitleEditText: EditText
    private lateinit var amountEditText: EditText
    private lateinit var addParticipantButton: Button
    private lateinit var saveButton: Button
    private lateinit var participantsRecyclerView: RecyclerView
    private lateinit var participantsAdapter: ParticipantsAdapter

    private val participantsList = mutableListOf<ParticipantSelection>()

    companion object {
        fun newInstance(groupId: String): AddBillFragment {
            val fragment = AddBillFragment()
            val args = Bundle()
            args.putString("groupId", groupId)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_bill, container, false)

        // Get Group ID or exit
        groupId = arguments?.getString("groupId") ?: run {
            Toast.makeText(requireContext(), "Error: Missing Group ID", Toast.LENGTH_SHORT).show()
            requireActivity().supportFragmentManager.popBackStack()
            return view
        }

        // Initialize views
        billTitleEditText = view.findViewById(R.id.billTitleEditText)
        amountEditText = view.findViewById(R.id.amountEditText)
        addParticipantButton = view.findViewById(R.id.addParticipantButton)
        saveButton = view.findViewById(R.id.saveBillButton)
        participantsRecyclerView = view.findViewById(R.id.participantsRecyclerView)

        // Setup RecyclerView
        participantsAdapter = ParticipantsAdapter(participantsList)
        participantsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        participantsRecyclerView.adapter = participantsAdapter

        // Add Participant Button
        addParticipantButton.setOnClickListener {
            participantsAdapter.addParticipant("") // Add empty participant
        }

        // Save Bill Button
        saveButton.setOnClickListener {
            try {
                saveBillToFirebase()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Unexpected error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    private fun saveBillToFirebase() {
        val title = billTitleEditText.text.toString().trim()
        val amount = amountEditText.text.toString().toDoubleOrNull()
        val participants = participantsAdapter.getParticipants()

        // Validate inputs
        if (title.isEmpty()) {
            Toast.makeText(requireContext(), "Bill title is required.", Toast.LENGTH_SHORT).show()
            return
        }

        if (amount == null || amount <= 0) {
            Toast.makeText(requireContext(), "Invalid bill amount.", Toast.LENGTH_SHORT).show()
            return
        }

        val paidBy = participants.find { it.isPaid }?.name
        if (paidBy.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "Select who paid for the bill.", Toast.LENGTH_SHORT).show()
            return
        }

        val owesParticipants = participants.filter { it.isOwes }
        if (owesParticipants.isEmpty()) {
            Toast.makeText(requireContext(), "Select at least one participant who owes.", Toast.LENGTH_SHORT).show()
            return
        }

        val individualShare = amount / owesParticipants.size
        val sanitizedParticipants = owesParticipants.associate {
            sanitizeKey(it.name) to individualShare
        }

        val database = FirebaseDatabase.getInstance().reference
        val billId = database.child("groups").child(groupId).child("bills").push().key ?: return

        val bill = mapOf(
            "title" to title,
            "amount" to amount,
            "paidBy" to paidBy,
            "participants" to sanitizedParticipants
        )

        database.child("groups").child(groupId).child("bills").child(billId).setValue(bill)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Bill saved successfully!", Toast.LENGTH_SHORT).show()
                requireActivity().supportFragmentManager.popBackStack()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to save bill: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun sanitizeKey(key: String): String {
        return key.replace(Regex("[./#$\\[\\]]"), "_")
    }
}
