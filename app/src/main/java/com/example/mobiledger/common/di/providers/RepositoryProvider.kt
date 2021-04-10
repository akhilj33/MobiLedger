package com.example.mobiledger.common.di.providers

import com.example.mobiledger.data.repository.AuthRepository
import com.example.mobiledger.data.repository.AuthRepositoryImpl
import com.example.mobiledger.data.repository.UserSettingsRepository
import com.example.mobiledger.data.repository.UserSettingsRepositoryImpl

class RepositoryProvider(
    apiSourceProvider: ApiSourceProvider,
    cacheSourceProvider: CacheSourceProvider
) {

    private val authRepository: AuthRepository by lazy {
        AuthRepositoryImpl(
            apiSourceProvider.provideFirebaseAuth()
        )
    }

    private val userSettingsRepository: UserSettingsRepository by lazy {
        UserSettingsRepositoryImpl(
            cacheSourceProvider.provideCacheSource()
        )
    }

    /*-------------------------------Public -----------------------------*/

    fun provideAuthRepository(): AuthRepository = authRepository

    fun provideUserSettingsRepository(): UserSettingsRepository = userSettingsRepository

}