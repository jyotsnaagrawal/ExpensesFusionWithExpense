package com.jyotsna.expensesfusion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
                for (groupSnapshot in snapshot.children) {
                    val groupId = groupSnapshot.key ?: continue
                    val groupName = groupSnapshot.child("name").getValue(String::class.java) ?: "Unknown Group"

                    val billsSnapshot = groupSnapshot.child("bills")
                    for (billSnapshot in billsSnapshot.children) {
                        val title = billSnapshot.child("title").getValue(String::class.java) ?: "No Title"
                        val amount = try {
                            billSnapshot.child("amount").getValue(Double::class.java)
                        } catch (e: Exception) {
                            billSnapshot.child("amount").getValue(String::class.java)?.toDoubleOrNull()
                        } ?: 0.0

                        val paidBy = billSnapshot.child("paidBy").getValue(String::class.java) ?: "Unknown"

                        // Parse participants safely
                        val participants = mutableMapOf<String, Double>()
                        for (participantSnapshot in billSnapshot.child("participants").children) {
                            val name = participantSnapshot.key ?: continue
                            val share = try {
                                participantSnapshot.getValue(Double::class.java)
                            } catch (e: Exception) {
                                participantSnapshot.getValue(String::class.java)?.toDoubleOrNull()
                            } ?: 0.0
                            participants[name] = share
                        }

                        val billSummary = BillSummary(
                            groupName = groupName,
                            title = title,
                            amount = amount,
                            paidBy = paidBy,
                            participants = participants
                        )
                        billSummaryList.add(billSummary)
                    }
                }
                billSummaryAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Failed to load bills: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

}