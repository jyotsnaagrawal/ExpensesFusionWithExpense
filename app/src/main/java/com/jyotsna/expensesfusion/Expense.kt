package com.jyotsna.expensesfusion

data class Expense(
    val name: String = "",
    val amount: Double = 0.0,
    val isTaxDeductible: Boolean = false
)

