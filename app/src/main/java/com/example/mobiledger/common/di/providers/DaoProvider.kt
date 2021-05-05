package com.example.mobiledger.common.di.providers

import com.example.mobiledger.data.sources.room.MobiLedgerDatabase
import com.example.mobiledger.data.sources.room.profile.ProfileDao
import com.example.mobiledger.data.sources.room.transaction.MonthlySummaryDao
import com.example.mobiledger.data.sources.room.transaction.TransactionDao

class DaoProvider(private val mobiLedgerDatabase: MobiLedgerDatabase) {
    fun provideProfileDao(): ProfileDao = mobiLedgerDatabase.profileDao()
    fun provideTransactionDao(): TransactionDao = mobiLedgerDatabase.transactionDao()
    fun provideMonthlySummaryDao(): MonthlySummaryDao = mobiLedgerDatabase.monthlySummaryDao()
}