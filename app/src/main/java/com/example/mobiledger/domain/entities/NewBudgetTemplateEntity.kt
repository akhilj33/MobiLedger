package com.example.mobiledger.domain.entities

import com.google.firebase.Timestamp

data class NewBudgetTemplateEntity(
    val name: String,
    val maxBudgetLimit: Long = 0,
    val transactionTime: Timestamp
) {
    constructor() : this(
        name = "",
        maxBudgetLimit = 0,
        transactionTime = Timestamp.now()
    )

    var id = Timestamp.now().seconds.toString()
}
