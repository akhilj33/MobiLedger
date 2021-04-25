package com.example.mobiledger.domain.entities

import com.google.firebase.Timestamp

data class TransactionEntity(
    val amount: Long? = null,
    val category: String? = null,
    val description: String? = null,
    val transactionType: String? = null,
    val transactionTime: Timestamp? = null
)
