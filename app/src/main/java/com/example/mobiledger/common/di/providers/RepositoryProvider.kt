package com.example.mobiledger.common.di.providers

import com.example.mobiledger.data.repository.*

class RepositoryProvider(
    authSourceProvider: AuthSourceProvider,
    apiSourceProvider: ApiSourceProvider,
    cacheSourceProvider: CacheSourceProvider,
    dbProvider: DbProvider,
    internetSourceProvider: InternetSourceProvider
) {

    private val internetRepository: InternetRepository by lazy {
        InternetRepositoryImpl(internetSourceProvider.provideInternetSource())
    }

    private val authRepository: AuthRepository by lazy {
        AuthRepositoryImpl(
            authSourceProvider.provideAuthSource()
        )
    }

    private val userSettingsRepository: UserSettingsRepository by lazy {
        UserSettingsRepositoryImpl(
            cacheSourceProvider.provideCacheSource()
        )
    }

    private val userRepository: UserRepository by lazy {
        UserRepositoryImpl(
            apiSourceProvider.provideUserApiSource()
        )
    }

    private val profileRepository: ProfileRepository by lazy {
        ProfileRepositoryImpl(
            apiSourceProvider.provideUserApiSource(),
            cacheSourceProvider.provideCacheSource(),
            dbProvider.provideProfileDb()
        )
    }

    private val transactionRepository: TransactionRepository by lazy {
        TransactionRepositoryImpl(
            apiSourceProvider.provideTransactionApiSource(),
            cacheSourceProvider.provideCacheSource(),
            dbProvider.provideTransactionDb()
        )
    }

    private val categoryRepository: CategoryRepository by lazy {
        CategoryRepositoryImpl(
            apiSourceProvider.provideCategoryApiSource(),
            cacheSourceProvider.provideCacheSource(),
            dbProvider.provideCategoryDb()
        )
    }

    private val budgetRepository: BudgetRepository by lazy {
        BudgetRepositoryImpl(
            apiSourceProvider.provideBudgetApiSource(),
            cacheSourceProvider.provideCacheSource(),
            dbProvider.provideCategoryDb()
        )
    }

    private val budgetTemplateRepository: BudgetTemplateRepository by lazy {
        BudgetTemplateRepositoryImpl(
            apiSourceProvider.provideBudgetTemplateApiSource(),
            cacheSourceProvider.provideCacheSource()
        )
    }

    /*-------------------------------Public -----------------------------*/

    fun provideInternetRepository(): InternetRepository = internetRepository

    fun provideAuthRepository(): AuthRepository = authRepository

    fun provideUserSettingsRepository(): UserSettingsRepository = userSettingsRepository

    fun provideUserRepository(): UserRepository = userRepository

    fun provideProfileRepository(): ProfileRepository = profileRepository

    fun provideTransactionRepository(): TransactionRepository = transactionRepository

    fun provideCategoryRepository(): CategoryRepository = categoryRepository

    fun provideBudgetRepository(): BudgetRepository = budgetRepository

    fun provideBudgetTemplateRepository(): BudgetTemplateRepository = budgetTemplateRepository
}