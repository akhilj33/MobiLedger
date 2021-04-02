package com.example.mobiledger.common.di.providers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mobiledger.presentation.auth.LoginViewModel
import com.example.mobiledger.presentation.auth.SignUpViewModel
import com.example.mobiledger.presentation.home.HomeViewModel
import com.example.mobiledger.presentation.main.MainActivityViewModel

@Suppress("UNCHECKED_CAST")
class ViewModelFactoryProvider(private val useCaseProvider: UseCaseProvider) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainActivityViewModel::class.java) -> {
                MainActivityViewModel(
                ) as T
            }

            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(
                    useCaseProvider.provideAuthUseCase()
                ) as T
            }

            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(
                    useCaseProvider.provideAuthUseCase()
                ) as T
            }

            modelClass.isAssignableFrom(SignUpViewModel::class.java) -> {
                SignUpViewModel(
                    useCaseProvider.provideAuthUseCase()
                ) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel Class")
        }
    }
}