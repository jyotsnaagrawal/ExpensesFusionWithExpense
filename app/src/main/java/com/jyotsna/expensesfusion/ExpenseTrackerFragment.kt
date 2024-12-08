package com.jyotsna.expensesfusion

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ExpenseTrackerFragment : Fragment() {

    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    private lateinit var pieChart: PieChart
    private lateinit var barChart: BarChart

    private lateinit var totalExpensesTextView: TextView
    private lateinit var firstExpenseTextView: TextView
    private lateinit var lastExpenseTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_expense_tracker, container, false)

        // Initialize Firebase
        database = FirebaseDatabase.getInstance().reference
        auth = FirebaseAuth.getInstance()

        // Find views
        pieChart = view.findViewById(R.id.pieChart)
        barChart = view.findViewById(R.id.barChart)
        totalExpensesTextView = view.findViewById(R.id.totalExpensesTextView)
        firstExpenseTextView = view.findViewById(R.id.firstExpenseTextView)
        lastExpenseTextView = view.findViewById(R.id.lastExpenseTextView)
        val addExpenseButton = view.findViewById<Button>(R.id.addExpenseButton)

        // Load expenses
        loadExpenses()

        // Add expense button click listener
        addExpenseButton.setOnClickListener {
            showAddExpenseDialog()
        }

        return view
    }

    private fun loadExpenses() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(requireContext(), "Please log in to view expenses.", Toast.LENGTH_SHORT).show()
            return
        }

        // Load expenses from Firebase
        database.child("expenses").child(currentUser.uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val expenses = mutableListOf<Map<String, Any>>()
                    for (expenseSnapshot in snapshot.children) {
                        val expense = expenseSnapshot.value as? Map<String, Any> ?: continue
                        expenses.add(expense)
                    }

                    if (expenses.isNotEmpty()) {
                        displayExpenses(expenses)
                    } else {
                        Toast.makeText(requireContext(), "No expenses found.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("ExpenseTracker", "Failed to load expenses: ${error.message}")
                }
            })
    }

    private fun displayExpenses(expenses: List<Map<String, Any>>) {
        // Calculate total expenses
        val total = expenses.sumOf { it["amount"].toString().toDouble() }
        totalExpensesTextView.text = "Total Expenses: $$total"

        // Display first and last expense dates
        val dates = expenses.map { it["date"].toString() }.sorted()
        firstExpenseTextView.text = "First Expense: ${dates.firstOrNull() ?: "N/A"}"
        lastExpenseTextView.text = "Last Expense: ${dates.lastOrNull() ?: "N/A"}"

        // Prepare data for charts
        val categoryData = expenses.groupBy { it["category"].toString() }
            .mapValues { entry -> entry.value.sumOf { it["amount"].toString().toDouble() } }

        val monthlyData = expenses.groupBy { it["date"].toString().substring(0, 7) } // Group by year-month
            .mapValues { entry -> entry.value.sumOf { it["amount"].toString().toDouble() } }

        // Update charts
        updatePieChart(categoryData)
        updateBarChart(monthlyData)
    }

    private fun updatePieChart(categoryData: Map<String, Double>) {
        val entries = categoryData.map { PieEntry(it.value.toFloat(), it.key) }
        val dataSet = PieDataSet(entries, "Expense Categories")
        dataSet.colors = listOf( // Set colors for each slice
            resources.getColor(R.color.purple_200),
            resources.getColor(R.color.teal_200),
            resources.getColor(R.color.yellow)
        )
        pieChart.data = PieData(dataSet)
        pieChart.description = Description().apply { text = "Category Summary" }
        pieChart.invalidate() // Refresh chart
    }

    private fun updateBarChart(monthlyData: Map<String, Double>) {
        val entries = monthlyData.entries.mapIndexed { index, entry ->
            BarEntry(index.toFloat(), entry.value.toFloat())
        }
        val dataSet = BarDataSet(entries, "Monthly Expenses")
        val barData = BarData(dataSet)
        barChart.data = barData
        barChart.description = Description().apply { text = "Monthly Summary" }
        barChart.invalidate() // Refresh chart
    }

    private fun showAddExpenseDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_expense, null)
        val amountEditText = dialogView.findViewById<EditText>(R.id.amountEditText)
        val categoryEditText = dialogView.findViewById<EditText>(R.id.categoryEditText)
        val dateEditText = dialogView.findViewById<EditText>(R.id.dateEditText)
        val descriptionEditText = dialogView.findViewById<EditText>(R.id.descriptionEditText)

        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Add Expense")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val amount = amountEditText.text.toString().toDoubleOrNull()
                val category = categoryEditText.text.toString()
                val date = dateEditText.text.toString()
                val description = descriptionEditText.text.toString()

                if (amount != null && category.isNotEmpty() && date.isNotEmpty()) {
                    saveExpense(amount, category, date, description)
                } else {
                    Toast.makeText(requireContext(), "Please fill in all fields.", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun saveExpense(amount: Double, category: String, date: String, description: String) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(requireContext(), "Please log in to save an expense.", Toast.LENGTH_SHORT).show()
            return
        }

        val expenseId = database.child("expenses").child(currentUser.uid).push().key
        if (expenseId == null) {
            Toast.makeText(requireContext(), "Failed to generate expense ID.", Toast.LENGTH_SHORT).show()
            return
        }

        val expense = mapOf(
            "amount" to amount,
            "category" to category,
            "date" to date,
            "description" to description
        )

        database.child("expenses").child(currentUser.uid).child(expenseId).setValue(expense)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Expense saved successfully!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                Log.e("ExpenseTracker", "Failed to save expense: ${exception.message}")
                Toast.makeText(requireContext(), "Failed to save expense.", Toast.LENGTH_SHORT).show()
            }
    }
}
