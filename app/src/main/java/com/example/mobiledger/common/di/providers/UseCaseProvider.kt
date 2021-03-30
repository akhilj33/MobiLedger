package com.example.mobiledger.common.di.providers

import com.example.mobiledger.domain.usecases.AuthUseCase
import com.example.mobiledger.domain.usecases.AuthUseCaseImpl

class UseCaseProvider(private val repositoryProvider: RepositoryProvider) {

    fun provideAuthUseCase(): AuthUseCase =
        AuthUseCaseImpl(
            repositoryProvider.provideAuthRepository()
        )
}