package com.jyotsna.expensesfusion.models

data class BillSummary(
    val groupName: String,
    val title: String,
    val amount: Double,
    val paidBy: String,
    val participants: Map<String, Double>
)
