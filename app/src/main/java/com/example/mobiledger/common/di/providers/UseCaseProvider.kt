package com.example.mobiledger.common.di.providers

import com.example.mobiledger.domain.usecases.AuthUseCase
import com.example.mobiledger.domain.usecases.AuthUseCaseImpl
import com.example.mobiledger.domain.usecases.UserSettingsUseCase
import com.example.mobiledger.domain.usecases.UserSettingsUseCaseImpl

class UseCaseProvider(private val repositoryProvider: RepositoryProvider) {

    fun provideAuthUseCase(): AuthUseCase =
        AuthUseCaseImpl(
            repositoryProvider.provideAuthRepository()
        )

    fun provideUserSettingsUseCase(): UserSettingsUseCase =
        UserSettingsUseCaseImpl(
            repositoryProvider.provideUserSettingsRepository()
        )
}