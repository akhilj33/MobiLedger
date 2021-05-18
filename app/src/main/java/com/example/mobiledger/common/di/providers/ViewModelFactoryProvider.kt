package com.example.mobiledger.common.di.providers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mobiledger.presentation.auth.LoginViewModel
import com.example.mobiledger.presentation.auth.SignUpViewModel
import com.example.mobiledger.presentation.categoryFragment.AddCategoryDialogViewModel
import com.example.mobiledger.presentation.categoryFragment.ExpenseCategoryViewModel
import com.example.mobiledger.presentation.categoryFragment.IncomeCategoryViewModel
import com.example.mobiledger.presentation.home.HomeViewModel
import com.example.mobiledger.presentation.main.MainActivityViewModel
import com.example.mobiledger.presentation.profile.EditProfileViewModel
import com.example.mobiledger.presentation.profile.ProfileViewModel
import com.example.mobiledger.presentation.addtransaction.AddTransactionDialogFragmentViewModel
import com.example.mobiledger.presentation.splash.SplashViewModel

@Suppress("UNCHECKED_CAST")
class ViewModelFactoryProvider(private val useCaseProvider: UseCaseProvider) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainActivityViewModel::class.java) -> {
                MainActivityViewModel() as T
            }

            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(
                    useCaseProvider.provideProfileUseCase(),
                    useCaseProvider.provideTransactionUseCase()
                ) as T
            }

            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(
                    useCaseProvider.provideAuthUseCase(),
                    useCaseProvider.provideUserSettingsUseCase(),
                    useCaseProvider.provideUserUseCase()
                ) as T
            }

            modelClass.isAssignableFrom(SignUpViewModel::class.java) -> {
                SignUpViewModel(
                    useCaseProvider.provideAuthUseCase(),
                    useCaseProvider.provideUserUseCase(),
                    useCaseProvider.provideUserSettingsUseCase(),
                    useCaseProvider.provideCategoryUseCase()
                ) as T
            }

            modelClass.isAssignableFrom(SplashViewModel::class.java) -> {
                SplashViewModel(useCaseProvider.provideUserSettingsUseCase()) as T
            }

            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> {
                ProfileViewModel(useCaseProvider.provideProfileUseCase()) as T
            }

            modelClass.isAssignableFrom(EditProfileViewModel::class.java) -> {
                EditProfileViewModel(useCaseProvider.provideProfileUseCase()) as T
            }
            modelClass.isAssignableFrom(AddTransactionDialogFragmentViewModel::class.java) -> {
                AddTransactionDialogFragmentViewModel(
                    useCaseProvider.provideTransactionUseCase(),
                    useCaseProvider.provideCategoryUseCase()
                ) as T
            }
            modelClass.isAssignableFrom(IncomeCategoryViewModel::class.java) -> {
                IncomeCategoryViewModel(useCaseProvider.provideCategoryUseCase()) as T
            }
            modelClass.isAssignableFrom(ExpenseCategoryViewModel::class.java) -> {
                ExpenseCategoryViewModel(useCaseProvider.provideCategoryUseCase()) as T
            }
            modelClass.isAssignableFrom(AddCategoryDialogViewModel::class.java) -> {
                AddCategoryDialogViewModel(useCaseProvider.provideCategoryUseCase()) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel Class")
        }
    }
}