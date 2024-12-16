package com.jyotsna.expensesfusion.adapters

data class Bill(
    val title: String = "",
    val amount: Double = 0.0,
    val paidBy: String = "", // Person who paid
    val participants: Map<String, Double> = emptyMap() // Participant name and their owed share
)