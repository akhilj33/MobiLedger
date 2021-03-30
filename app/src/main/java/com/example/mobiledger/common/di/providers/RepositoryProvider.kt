package com.example.mobiledger.common.di.providers

import com.example.mobiledger.data.repository.AuthRepository
import com.example.mobiledger.data.repository.AuthRepositoryImpl

class RepositoryProvider(
    apiSourceProvider: ApiSourceProvider
) {

    private val authRepository: AuthRepository by lazy {
        AuthRepositoryImpl(
            apiSourceProvider.provideFirebaseAuth()
        )
    }
    /*-------------------------------Public -----------------------------*/

    fun provideAuthRepository(): AuthRepository = authRepository
}