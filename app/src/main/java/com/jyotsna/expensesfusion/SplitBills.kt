package com.jyotsna.expensesfusion

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SplitActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.split_bills)

        val editTextAmount = findViewById<EditText>(R.id.editTextAmount)
        val editTextNumPeople = findViewById<EditText>(R.id.editTextNumPeople)
        val buttonCalculate = findViewById<Button>(R.id.buttonCalculate)
        val textViewResult = findViewById<TextView>(R.id.textViewResult)

        buttonCalculate.setOnClickListener {
            val amount = editTextAmount.text.toString().toDoubleOrNull()
            val numPeople = editTextNumPeople.text.toString().toIntOrNull()

            if (amount != null && numPeople != null && numPeople > 0) {
                val splitAmount = amount / numPeople
                textViewResult.text = buildString {
                    append("Each person pays: ")
                    append(splitAmount)
                }
                textViewResult.visibility = TextView.VISIBLE
            } else {
                textViewResult.text = buildString {
                    append("Invalid input")
                }
                textViewResult.visibility = TextView.VISIBLE
            }
        }
    }
}
