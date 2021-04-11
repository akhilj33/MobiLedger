package com.example.mobiledger.common.di.providers

import com.example.mobiledger.data.repository.*

class RepositoryProvider(
    authSourceProvider: AuthSourceProvider,
    apiSourceProvider: ApiSourceProvider,
    cacheSourceProvider: CacheSourceProvider
) {

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
        UserRepositoryImpl(apiSourceProvider.provideUserApiSource()
        )
    }

    /*-------------------------------Public -----------------------------*/

    fun provideAuthRepository(): AuthRepository = authRepository

    fun provideUserSettingsRepository(): UserSettingsRepository = userSettingsRepository

    fun provideUserRepository(): UserRepository = userRepository

}