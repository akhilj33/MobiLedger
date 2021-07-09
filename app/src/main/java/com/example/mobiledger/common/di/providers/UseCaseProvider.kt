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

    fun provideProfileUseCase(): ProfileUseCase =
        ProfileUseCaseImpl(repositoryProvider.provideProfileRepository())

    fun provideTransactionUseCase(): TransactionUseCase =
        TransactionUseCaseImpl(repositoryProvider.provideTransactionRepository())

    fun provideCategoryUseCase(): CategoryUseCase =
        CategoryUseCaseImpl(repositoryProvider.provideCategoryRepository())

    fun provideBudgetUseCase(): BudgetUseCase =
        BudgetUseCaseImpl(repositoryProvider.provideBudgetRepository())

    fun provideBudgetTemplateUseCase(): BudgetTemplateUseCase =
        BudgetTemplateUseCaseImpl(repositoryProvider.provideBudgetTemplateRepository())

    fun provideInternetUseCase(): InternetUseCase = InternetUseCaseImpl(repositoryProvider.provideInternetRepository())

    fun provideAttachmentUseCase(): AttachmentUseCase = AttachmentUseCaseImpl(repositoryProvider.provideAttachmentRepository(), provideProfileUseCase())


}