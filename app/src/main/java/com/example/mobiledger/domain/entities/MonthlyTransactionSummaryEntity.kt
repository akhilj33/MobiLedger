package com.example.mobiledger.domain.entities

import com.example.mobiledger.common.utils.ConstantUtils.NO_OF_EXPENSE_TRANSACTION
import com.example.mobiledger.common.utils.ConstantUtils.NO_OF_INCOME_TRANSACTION
import com.example.mobiledger.common.utils.ConstantUtils.NO_OF_TRANSACTION
import com.example.mobiledger.common.utils.ConstantUtils.TOTAL_BALANCE
import com.example.mobiledger.common.utils.ConstantUtils.TOTAL_EXPENSE
import com.example.mobiledger.common.utils.ConstantUtils.TOTAL_INCOME

data class MonthlyTransactionSummaryEntity(
    val noOfTransaction: Long = 0,
    val noOfIncomeTransaction: Long = 0,
    val noOfExpenseTransaction: Long = 0,
    val totalBalance: Long = 0,
    val totalIncome: Long = 0,
    val totalExpense: Long = 0
)

fun MonthlyTransactionSummaryEntity.isEmpty(): Boolean = this == MonthlyTransactionSummaryEntity()

fun MonthlyTransactionSummaryEntity.toMutableMap(): MutableMap<String, Any> = mutableMapOf(
    NO_OF_TRANSACTION to noOfTransaction,
    NO_OF_INCOME_TRANSACTION to noOfIncomeTransaction,
    NO_OF_EXPENSE_TRANSACTION to noOfExpenseTransaction,
    TOTAL_BALANCE to totalBalance,
    TOTAL_INCOME to totalIncome,
    TOTAL_EXPENSE to totalExpense
)
