package com.example.mobiledger.data.sources.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.mobiledger.common.utils.RoomConverters
import com.example.mobiledger.data.sources.room.categories.*
import com.example.mobiledger.data.sources.room.profile.ProfileDao
import com.example.mobiledger.data.sources.room.profile.UserRoomItem
import com.example.mobiledger.data.sources.room.transaction.MonthlySummaryDao
import com.example.mobiledger.data.sources.room.transaction.MonthlySummaryRoomItem
import com.example.mobiledger.data.sources.room.transaction.TransactionDao
import com.example.mobiledger.data.sources.room.transaction.TransactionRoomItem


@Database(
    entities = [UserRoomItem::class, TransactionRoomItem::class, MonthlySummaryRoomItem::class, CategoryRoomItem::class,
        MonthlyCategorySummaryRoomItem::class, CategoriesTransactionsRoomItem::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(RoomConverters::class)
abstract class MobiLedgerDatabase : RoomDatabase() {
    abstract fun profileDao(): ProfileDao
    abstract fun transactionDao(): TransactionDao
    abstract fun monthlySummaryDao(): MonthlySummaryDao
    abstract fun categoryDao(): CategoryDao
    abstract fun monthlyCategorySummaryDao(): MonthlyCategorySummaryDao
    abstract fun categoryTransactionsRefDao(): CategoryTransactionsRefDao
}