package com.example.mobiledger.data.sources.room.transaction

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TransactionDao {
    @Query("SELECT count(*) FROM transaction_table")
    suspend fun hasTransactions(): Int?

    @Query("SELECT * FROM transaction_table")
    suspend fun fetchAllTransactions(): List<TransactionRoomItem>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveTransaction(transactionItem: TransactionRoomItem)

    @Query("DELETE FROM transaction_table")
    suspend fun deleteAllTransactions()
}

@Dao
interface MonthlySummaryDao {
    @Query("SELECT count(*) FROM monthly_summary WHERE monthYear = :monthYear")
    suspend fun hasMonthlySummary(monthYear: String): Int?

    @Query("SELECT * FROM monthly_summary WHERE monthYear = :monthYear")
    suspend fun fetchMonthlySummary(monthYear: String): MonthlySummaryRoomItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveMonthlySummary(monthlySummary: MonthlySummaryRoomItem)

    @Query("DELETE FROM monthly_summary")
    suspend fun deleteMonthSummary()
}