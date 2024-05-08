package com.jyotsna.expensesfusion

import ExpenseAdapter
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore


class ExpenseTrackerActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val expenses = mutableListOf<Expense>()
    private lateinit var expenseAdapter: ExpenseAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.expense_tracker)

        val recyclerViewExpenses = findViewById<RecyclerView>(R.id.recyclerViewExpenses)
        recyclerViewExpenses.layoutManager = LinearLayoutManager(this)
        expenseAdapter = ExpenseAdapter(expenses)
        recyclerViewExpenses.adapter = expenseAdapter

        val editTextExpenseName = findViewById<EditText>(R.id.editTextExpenseName)
        val editTextExpenseAmount = findViewById<EditText>(R.id.editTextExpenseAmount)
        val checkBoxTaxDeductible = findViewById<CheckBox>(R.id.checkBoxTaxDeductible)
        val buttonAddExpense = findViewById<Button>(R.id.buttonAddExpense)

        buttonAddExpense.setOnClickListener {
            val name = editTextExpenseName.text.toString()
            val amount = editTextExpenseAmount.text.toString().toDoubleOrNull()
            val isTaxDeductible = checkBoxTaxDeductible.isChecked

            if (amount != null) {
                val expense = Expense(name, amount, isTaxDeductible)
                saveExpenseToFirestore(expense)
                clearFields()
                Toast.makeText(this, "Expense added successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Please enter a valid expense amount", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveExpenseToFirestore(expense: Expense) {
        db.collection("expenses")
            .add(expense)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "Expense added with ID: ${documentReference.id}")
                expenses.add(expense)
                expenseAdapter.notifyDataSetChanged()
                updateSummary()
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding expense", e)
            }
    }

    private fun updateSummary() {
        val totalExpenses = expenses.size
        val totalAmount = expenses.sumOf { it.amount }
        val averageAmount = totalAmount / totalExpenses

        findViewById<TextView>(R.id.textViewTotalExpenses).text = buildString {
        append("Total Expenses: ")
        append(totalExpenses)
    }
        findViewById<TextView>(R.id.textViewTotalAmount).text = buildString {
        append("Total Amount: ")
        append(totalAmount)
    }
        findViewById<TextView>(R.id.textViewAverageAmount).text = buildString {
        append("Average Amount: ")
        append(averageAmount)
    }
    }

    private fun clearFields() {
        findViewById<EditText>(R.id.editTextExpenseName).text.clear()
        findViewById<EditText>(R.id.editTextExpenseAmount).text.clear()
        findViewById<CheckBox>(R.id.checkBoxTaxDeductible).isChecked = false
    }

    companion object {
        private const val TAG = "ExpenseTrackerActivity"
    }
}
