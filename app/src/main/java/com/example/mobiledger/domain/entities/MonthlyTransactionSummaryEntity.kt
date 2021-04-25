package com.example.mobiledger.domain.entities

data class MonthlyTransactionSummaryEntity(
    val noOfTransaction: Long? = null,
    val noOfIncomeTransaction: Long? = null,
    val noOfExpenseTransaction: Long? = null,
    val totalBalance: Long? = null,
    val totalIncome: Long? = null,
    val totalExpense: Long? = null
)
