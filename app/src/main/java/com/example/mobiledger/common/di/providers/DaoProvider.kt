package com.example.mobiledger.common.di.providers

import com.example.mobiledger.data.sources.room.MobiLedgerDatabase
import com.example.mobiledger.data.sources.room.categories.CategoryDao
import com.example.mobiledger.data.sources.room.categories.CategoryTransactionsRefDao
import com.example.mobiledger.data.sources.room.categories.MonthlyCategorySummaryDao
import com.example.mobiledger.data.sources.room.profile.ProfileDao
import com.example.mobiledger.data.sources.room.transaction.MonthlySummaryDao
import com.example.mobiledger.data.sources.room.transaction.TransactionDao

class DaoProvider(private val mobiLedgerDatabase: MobiLedgerDatabase) {
    fun provideProfileDao(): ProfileDao = mobiLedgerDatabase.profileDao()
    fun provideTransactionDao(): TransactionDao = mobiLedgerDatabase.transactionDao()
    fun provideMonthlySummaryDao(): MonthlySummaryDao = mobiLedgerDatabase.monthlySummaryDao()
    fun provideCategoryDao(): CategoryDao = mobiLedgerDatabase.categoryDao()
    fun provideCategorySummaryDao(): MonthlyCategorySummaryDao = mobiLedgerDatabase.monthlyCategorySummaryDao()
    fun provideCategoryRefDao(): CategoryTransactionsRefDao = mobiLedgerDatabase.categoryTransactionsRefDao()
    fun provideDb(): MobiLedgerDatabase = mobiLedgerDatabase
}