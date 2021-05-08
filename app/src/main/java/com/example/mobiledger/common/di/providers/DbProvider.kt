package com.example.mobiledger.common.di.providers

import com.example.mobiledger.data.sources.room.categories.CategoriesDb
import com.example.mobiledger.data.sources.room.categories.CategoryDbImpl
import com.example.mobiledger.data.sources.room.profile.ProfileDb
import com.example.mobiledger.data.sources.room.profile.ProfileDbImpl
import com.example.mobiledger.data.sources.room.transaction.TransactionDb
import com.example.mobiledger.data.sources.room.transaction.TransactionDbImpl

class DbProvider(private val daoProvider: DaoProvider) {
    fun provideProfileDb(): ProfileDb = ProfileDbImpl(daoProvider.provideProfileDao())

    fun provideTransactionDb(): TransactionDb =
        TransactionDbImpl(daoProvider.provideTransactionDao(), daoProvider.provideMonthlySummaryDao())

    fun provideCategoryDb(): CategoriesDb =
        CategoryDbImpl(daoProvider.provideCategoryDao())


}