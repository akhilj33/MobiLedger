package com.example.mobiledger.domain.entities

import com.example.mobiledger.domain.enums.TransactionType
import com.google.firebase.Timestamp

data class TransactionEntity(
    val amount: Long,
    val category: String,
    val description: String? = null,
    val transactionType: TransactionType,
    val transactionTime: Timestamp
) {
    val id = transactionTime.seconds.toString()
}
