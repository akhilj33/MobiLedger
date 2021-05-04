package com.example.mobiledger.common.di.providers

import com.example.mobiledger.data.sources.room.profile.ProfileDb
import com.example.mobiledger.data.sources.room.profile.ProfileDbImpl

class DbProvider(private val daoProvider: DaoProvider) {

    fun provideProfileDb(): ProfileDb = ProfileDbImpl(daoProvider.provideProfileDao())
}