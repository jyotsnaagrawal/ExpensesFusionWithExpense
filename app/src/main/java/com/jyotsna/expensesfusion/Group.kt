package com.jyotsna.expensesfusion.models

import com.jyotsna.expensesfusion.adapters.Bill

data class Group(
    val id: String = "",
    val name: String = "",
    val bills: Map<String, Bill>? = null
)
