package com.example.mobiledger.common.di.providers

import com.example.mobiledger.data.sources.room.MobiLedgerDatabase
import com.example.mobiledger.data.sources.room.profile.ProfileDao

class DaoProvider(private val mobiLedgerDatabase: MobiLedgerDatabase) {
    fun provideProfileDao(): ProfileDao = mobiLedgerDatabase.profileDao()
}