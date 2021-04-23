package com.example.mobiledger.domain.enums

enum class TransactionType(val type: String) {
    Expense("expense"), Income("income");

    companion object {
        fun getTransactionType(type: String?): TransactionType? {
            return when (type) {
                Expense.type -> Expense
                Income.type -> Income
                else -> null
            }
        }
    }
}