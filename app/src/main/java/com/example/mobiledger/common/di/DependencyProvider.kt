package com.example.mobiledger.common.di

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import com.example.mobiledger.common.di.providers.*

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
    private val firebaseAuthProvider : FirebaseAuthProvider = FirebaseAuthProvider()

    private val apiSourceProvider: ApiSourceProvider by lazy {
        ApiSourceProvider(
            retrofitProvider,
            firebaseAuthProvider
        )
    }

    private val cacheSourceProvider: CacheSourceProvider by lazy {
        CacheSourceProvider(
            provideApplicationContext()
        )
    }
    /*-------------------------------Repository------------------------------------------*/

    private val repositoryProvider: RepositoryProvider by lazy {
        RepositoryProvider(
            apiSourceProvider
        )
    }

    /*-------------------------------Use Case------------------------------------------*/

    private val useCaseProvider: UseCaseProvider by lazy { UseCaseProvider(repositoryProvider) }

    private val viewModelFactory: ViewModelProvider.Factory by lazy {
        ViewModelFactoryProvider(
            useCaseProvider
        )
    }

    /*-------------------------------Public Providers------------------------------------------*/

    private fun provideApplicationContext() = applicationContext

    fun provideViewModelFactory(): ViewModelProvider.Factory = viewModelFactory

    fun provideUseCaseProvider(): UseCaseProvider = useCaseProvider

}