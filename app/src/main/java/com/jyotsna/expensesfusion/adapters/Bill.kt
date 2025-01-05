package com.jyotsna.expensesfusion.adapters

data class Bill(
    val groupName: String = "",
    val title: String = "",
    val amount: Double = 0.0,
    val paidBy: String = "",
    val participants: Map<String, Double> = emptyMap(),
    val paymentStatus: String = "Pending" //
)