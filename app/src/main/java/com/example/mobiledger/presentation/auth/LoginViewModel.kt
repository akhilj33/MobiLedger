package com.example.mobiledger.presentation.auth

import androidx.lifecycle.viewModelScope
import com.example.mobiledger.common.base.BaseViewModel
import com.example.mobiledger.domain.AppResult
import com.example.mobiledger.domain.usecases.AuthUseCase
import kotlinx.coroutines.launch

class LoginViewModel(private val authUseCase: AuthUseCase) : BaseViewModel() {

    fun loginUserViaEmail(){
        viewModelScope.launch {
            when(val result = authUseCase.loginViaEmail("test2@gmail.com", "test123")){
                is AppResult.Success -> {
                val a = result.data
                }
                is AppResult.Failure -> {
                    val error = result.error
                }
            }
        }
    }
}
