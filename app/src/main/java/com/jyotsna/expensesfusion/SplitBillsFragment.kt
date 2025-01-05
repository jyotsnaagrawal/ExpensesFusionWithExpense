package com.jyotsna.expensesfusion

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.jyotsna.expensesfusion.adapters.BillSummaryAdapter
import com.jyotsna.expensesfusion.models.BillSummary

class SplitBillsFragment : Fragment() {

    private lateinit var database: DatabaseReference
    private lateinit var billSummaryRecyclerView: RecyclerView
    private lateinit var billSummaryAdapter: BillSummaryAdapter
    private val billSummaryList = mutableListOf<BillSummary>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_split_bills, container, false)

        // Initialize Firebase Database reference
        database = FirebaseDatabase.getInstance().reference

        // Initialize RecyclerView
        billSummaryRecyclerView = view.findViewById(R.id.billSummaryRecyclerView)
        billSummaryRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        billSummaryAdapter = BillSummaryAdapter(billSummaryList)
        billSummaryRecyclerView.adapter = billSummaryAdapter

        // Load Split Bill Summary
        loadSplitBillSummary()

        return view
    }

    private fun loadSplitBillSummary() {
        database.child("groups").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                billSummaryList.clear()
                Log.d("SplitBillsFragment", "Data fetched from Firebase: ${snapshot.childrenCount} groups")

                for (groupSnapshot in snapshot.children) {
                    val groupName = groupSnapshot.child("name").getValue(String::class.java) ?: "Unknown Group"
                    Log.d("SplitBillsFragment", "Processing group: $groupName")

                    val billsSnapshot = groupSnapshot.child("bills")
                    for (billSnapshot in billsSnapshot.children) {
                        try {
                            val title = billSnapshot.child("title").getValue(String::class.java) ?: "No Title"
                            val amount = billSnapshot.child("amount").getValue(Double::class.java) ?: 0.0
                            val paidBy = billSnapshot.child("paidBy").getValue(String::class.java) ?: "Unknown"

                            val participants = billSnapshot.child("participants").children.associate {
                                val name = it.key ?: "Unknown"
                                val value = it.getValue(Double::class.java) ?: 0.0
                                name to value
                            }

                            val paymentStatus = if (participants.values.sum() >= amount) "Paid" else "Pending"
                            Log.d("SplitBillsFragment", "Bill processed: $title, Amount: $amount, Paid By: $paidBy, Participants: $participants, Status: $paymentStatus")

                            val billSummary = BillSummary(
                                groupName = groupName,
                                title = title,
                                amount = amount,
                                paidBy = paidBy,
                                participants = participants,
                                paymentStatus = paymentStatus
                            )
                            billSummaryList.add(billSummary)

                        } catch (e: Exception) {
                            Log.e("SplitBillsFragment", "Error processing bill data: ${e.message}", e)
                        }
                    }
                }

                billSummaryAdapter.notifyDataSetChanged()
                Log.d("SplitBillsFragment", "Bill summary loaded successfully with ${billSummaryList.size} items.")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("SplitBillsFragment", "Failed to load bills: ${error.message}")
            }
        })
    }
}
