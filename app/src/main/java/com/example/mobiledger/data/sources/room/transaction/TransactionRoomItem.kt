package com.example.mobiledger.data.sources.room.transaction

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.mobiledger.domain.enums.TransactionType
import com.google.firebase.Timestamp

@Entity(tableName = "transaction_table")
data class TransactionRoomItem(
    @PrimaryKey @ColumnInfo(name = "id") val uId: String,
    @ColumnInfo(name = "monthYear") val monthYear: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "amount") val amount: Long,
    @ColumnInfo(name = "category") val category: String,
    @ColumnInfo(name = "description") val description: String? = null,
    @ColumnInfo(name = "transactionType") val transactionType: TransactionType,
    @ColumnInfo(name = "transactionTime") val transactionTime: Timestamp
)
