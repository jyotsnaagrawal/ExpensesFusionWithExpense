package com.jyotsna.expensesfusion

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()

        // Check if the user is logged in
        val currentUser = auth.currentUser
        if (currentUser == null) {
            // If not logged in, redirect to SignInActivity
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
            return
        }

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        // Safely load the default fragment
        if (savedInstanceState == null) {
            loadFragment(SplitBillsFragment())
        }

        // Handle bottom navigation item clicks
        bottomNavigationView.setOnItemSelectedListener { item ->
            val fragment = when (item.itemId) {
                R.id.navigation_split_bills -> SplitBillsFragment()
                R.id.navigation_expense_tracker -> ExpenseTrackerFragment()
                R.id.navigation_groups -> GroupsFragment()
                else -> null
            }
            fragment?.let { loadFragment(it) }
            true
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
