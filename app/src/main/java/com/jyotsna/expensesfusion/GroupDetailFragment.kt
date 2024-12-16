package com.jyotsna.expensesfusion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
    private lateinit var addBillButton: Button
    private lateinit var billsAdapter: BillsAdapter
    private lateinit var groupId: String

    private val billsList = mutableListOf<Bill>()

    companion object {
        fun newInstance(groupId: String): GroupDetailFragment {
            val fragment = GroupDetailFragment()
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
            navigateToAddBill()
        }

        return view
    }

    private fun navigateToAddBill() {
        val addBillFragment = AddBillFragment.newInstance(groupId)
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, addBillFragment)
            .addToBackStack(null)
            .commit()
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
                    Toast.makeText(requireContext(), "Failed to load bills.", Toast.LENGTH_SHORT)
                        .show()
                }
            })
    }
}