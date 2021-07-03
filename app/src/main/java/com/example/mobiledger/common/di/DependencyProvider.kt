package com.example.mobiledger.common.di

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.example.mobiledger.common.di.providers.*
import com.example.mobiledger.data.sources.room.MobiLedgerDatabase

/**
 * Provides Constructor dependency to all providers which in turn provides constructor dependencies
 * to all sources like api sources, various other sources, repository, usecases, viewmodels i.e.
 * it provides dependency to all levels of MVVM
 **/
object DependencyProvider {
    private lateinit var applicationContext: Context

    fun inject(context: Context) {
        applicationContext = context
    }

    /*-------------------------------Sources------------------------------------------*/

    private val retrofitProvider: RetrofitProvider = RetrofitProvider()
    private val firebaseProvider: FirebaseProvider = FirebaseProvider()

    private val authSourceProvider: AuthSourceProvider by lazy {
        AuthSourceProvider(firebaseProvider)
    }

    private val internetSourceProvider: InternetSourceProvider by lazy {
        InternetSourceProvider(provideApplicationContext())
    }

    private val apiSourceProvider: ApiSourceProvider by lazy {
        ApiSourceProvider(retrofitProvider, firebaseProvider, authSourceProvider)
    }

    private val cacheSourceProvider: CacheSourceProvider by lazy {
        CacheSourceProvider(provideApplicationContext())
    }

    private val mobiLedgerDatabase by lazy {
        Room.databaseBuilder(provideApplicationContext(), MobiLedgerDatabase::class.java, "mobi-ledger-db").build()
    }
    /*-------------------------------Repository------------------------------------------*/

    private val repositoryProvider: RepositoryProvider by lazy {
        RepositoryProvider(
            authSourceProvider, apiSourceProvider, cacheSourceProvider, dbProvider,
            internetSourceProvider
        )
    }

    /*-------------------------------Use Case------------------------------------------*/

    private val useCaseProvider: UseCaseProvider by lazy { UseCaseProvider(repositoryProvider) }

    private val viewModelFactory: ViewModelProvider.Factory by lazy {
        ViewModelFactoryProvider(useCaseProvider)
    }

    /*-------------------------------Room Dao------------------------------------------*/

    private val daoProvider: DaoProvider by lazy {
        DaoProvider(mobiLedgerDatabase)
    }

    /*-------------------------------Room Db------------------------------------------*/

    private val dbProvider: DbProvider by lazy {
        DbProvider(daoProvider, firebaseProvider)
    }

    /*-------------------------------Public Providers------------------------------------------*/

    private fun provideApplicationContext() = applicationContext

    fun provideViewModelFactory(): ViewModelProvider.Factory = viewModelFactory

    fun provideUseCaseProvider(): UseCaseProvider = useCaseProvider

}