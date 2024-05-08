package com.jyotsna.expensesfusion

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        firebaseAuth = FirebaseAuth.getInstance()

        if (firebaseAuth.currentUser == null) {
            // If user is not logged in, navigate to SignInActivity
            startActivity(Intent(this, SignInActivity::class.java))
            finish() // Finish MainActivity to prevent returning to it via Back button
        }

        // Initialize bottom navigation view
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    // Handle Home fragment or activity
                    true
                }
                R.id.navigation_split_bills -> {
                    // Navigate to SplitBillsActivity
                    startActivity(Intent(this, SplitActivity::class.java))
                    true
                }
                R.id.navigation_expense_tracker -> {
                    // Navigate to ExpenseTrackerActivity
                    startActivity(Intent(this, ExpenseTrackerActivity::class.java))
                    true
                }
                else -> false
            }
        }

        // Set background color for bottom navigation view
        //bottomNavigationView.setBackgroundColor(ContextCompat.getColor(this, R.color.yellow))

        // Apply window insets to the main layout to handle system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }}
