package com.jyotsna.expensesfusion

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        // Navigate to SplitBillsFragment by default
        loadFragment(SplitBillsFragment())
        bottomNavigationView.selectedItemId = R.id.navigation_split_bills

        // Handle navigation item selection
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_split_bills -> {
                    loadFragment(SplitBillsFragment())
                    true
                }
                R.id.navigation_expense_tracker -> {
                    loadFragment(ExpenseTrackerFragment())
                    true
                }
                R.id.navigation_groups -> {
                    loadFragment(GroupsFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
