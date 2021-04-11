package com.example.mobiledger.common.di.providers

import com.example.mobiledger.domain.usecases.*

class UseCaseProvider(private val repositoryProvider: RepositoryProvider) {

    fun provideAuthUseCase(): AuthUseCase =
        AuthUseCaseImpl(
            repositoryProvider.provideAuthRepository()
        )

    fun provideUserSettingsUseCase(): UserSettingsUseCase =
        UserSettingsUseCaseImpl(
            repositoryProvider.provideUserSettingsRepository()
        )

    fun provideUserUseCase(): UserUseCase =
        UserUseCaseImpl(repositoryProvider.provideUserRepository())
}