package com.jyotsna.expensesfusion

import android.app.DatePickerDialog
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
import java.util.*

class ExpenseTrackerFragment : Fragment() {

    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    private lateinit var pieChart: PieChart
    private lateinit var barChart: BarChart
    private lateinit var totalExpensesTextView: TextView
    private lateinit var firstExpenseTextView: TextView
    private lateinit var lastExpenseTextView: TextView
    private lateinit var dateFilterEditText: EditText
    private lateinit var filterButton: Button

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
        dateFilterEditText = view.findViewById(R.id.dateFilterEditText)
        filterButton = view.findViewById(R.id.filterButton)
        val addExpenseButton = view.findViewById<Button>(R.id.addExpenseButton)

        // Set up Date Picker for filter input
        dateFilterEditText.setOnClickListener {
            showDatePicker()
        }

        // Load all expenses initially
        loadExpenses()

        // Filter expenses when "Filter" button is clicked
        filterButton.setOnClickListener {
            val selectedDate = dateFilterEditText.text.toString().trim()
            if (selectedDate.isNotEmpty()) {
                filterExpensesByDate(selectedDate)
            } else {
                Toast.makeText(requireContext(), "Please select a date.", Toast.LENGTH_SHORT).show()
            }
        }

        // Show "Add Expense" dialog on button click
        addExpenseButton.setOnClickListener {
            showAddExpenseDialog()
        }

        return view
    }

    // Show a date picker for filter input
    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
            val selectedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
            dateFilterEditText.setText(selectedDate)
        }, year, month, day).show()
    }

    // Filter expenses by selected date
    private fun filterExpensesByDate(selectedDate: String) {
        val currentUser = auth.currentUser ?: return
        database.child("expenses").child(currentUser.uid)
            .orderByChild("date")
            .equalTo(selectedDate)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val expenses = snapshot.children.mapNotNull {
                        it.value as? Map<String, Any>
                    }
                    if (expenses.isNotEmpty()) {
                        displayExpenses(expenses)
                    } else {
                        Toast.makeText(requireContext(), "No expenses found for $selectedDate", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("ExpenseTracker", "Failed to filter expenses: ${error.message}")
                }
            })
    }

    // Load all expenses from Firebase
    private fun loadExpenses() {
        val currentUser = auth.currentUser ?: return
        database.child("expenses").child(currentUser.uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val expenses = snapshot.children.mapNotNull {
                        it.value as? Map<String, Any>
                    }
                    displayExpenses(expenses)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("ExpenseTracker", "Failed to load expenses: ${error.message}")
                }
            })
    }

    // Display expenses in charts and text views
    private fun displayExpenses(expenses: List<Map<String, Any>>) {
        // Total expenses
        val total = expenses.sumOf { it["amount"].toString().toDouble() }
        totalExpensesTextView.text = "Total Expenses: $$total"

        // First and last expense dates
        val dates = expenses.map { it["date"].toString() }.sorted()
        firstExpenseTextView.text = "First Expense: ${dates.firstOrNull() ?: "N/A"}"
        lastExpenseTextView.text = "Last Expense: ${dates.lastOrNull() ?: "N/A"}"

        // Group by category
        val categoryData = expenses.groupBy { it["category"].toString() }
            .mapValues { it.value.sumOf { expense -> expense["amount"].toString().toDouble() } }

        // Update pie chart
        updatePieChart(categoryData)
    }

    private fun updatePieChart(categoryData: Map<String, Double>) {
        val entries = categoryData.map { PieEntry(it.value.toFloat(), it.key) }
        val dataSet = PieDataSet(entries, "Categories")
        dataSet.colors = listOf(
            resources.getColor(R.color.purple_200),
            resources.getColor(R.color.teal_200),
            resources.getColor(R.color.yellow)
        )
        pieChart.data = PieData(dataSet)
        pieChart.description = Description().apply { text = "Expenses by Category" }
        pieChart.invalidate()
    }

    private fun showAddExpenseDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_expense, null)
        val amountEditText = dialogView.findViewById<EditText>(R.id.amountEditText)
        val categoryEditText = dialogView.findViewById<EditText>(R.id.categoryEditText)
        val dateEditText = dialogView.findViewById<EditText>(R.id.dateEditText)
        val descriptionEditText = dialogView.findViewById<EditText>(R.id.descriptionEditText)

        dateEditText.setOnClickListener { showDatePicker() }

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
        val currentUser = auth.currentUser ?: return
        val expenseId = database.child("expenses").child(currentUser.uid).push().key ?: return

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
            .addOnFailureListener { e ->
                Log.e("ExpenseTracker", "Failed to save expense: ${e.message}")
            }
    }
}
