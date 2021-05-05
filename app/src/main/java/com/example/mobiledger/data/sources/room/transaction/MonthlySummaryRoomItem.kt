package com.example.mobiledger.data.sources.room.transaction

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "monthly_summary")
data class MonthlySummaryRoomItem(
    @PrimaryKey @ColumnInfo(name = "monthYear") val monthYear: String,
    @ColumnInfo(name = "noOfTransaction") val noOfTransaction: Long,
    @ColumnInfo(name = "noOfIncomeTransaction") val noOfIncomeTransaction: Long,
    @ColumnInfo(name = "noOfExpenseTransaction") val noOfExpenseTransaction: Long,
    @ColumnInfo(name = "totalBalance") val totalBalance: Long,
    @ColumnInfo(name = "totalIncome") val totalIncome: Long,
    @ColumnInfo(name = "totalExpense") val totalExpense: Long
)
