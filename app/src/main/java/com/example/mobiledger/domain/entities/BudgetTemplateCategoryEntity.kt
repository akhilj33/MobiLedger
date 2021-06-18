package com.example.mobiledger.domain.entities

data class BudgetTemplateCategoryEntity(
    val category: String,
    val categoryBudget: Long
) {
    constructor() : this(
        category = "",
        categoryBudget = 0
    )
}
