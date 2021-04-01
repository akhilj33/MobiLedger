package com.example.mobiledger.presentation.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.mobiledger.common.base.BaseViewModel
import com.example.mobiledger.domain.AppError
import com.example.mobiledger.domain.AppResult
import com.example.mobiledger.domain.entities.AuthEntity
import com.example.mobiledger.domain.usecases.AuthUseCase
import com.example.mobiledger.presentation.Event
import kotlinx.coroutines.launch

class SignUpViewModel(private val authUseCase: AuthUseCase) : BaseViewModel() {

    val signUpResult: LiveData<Event<AuthEntity>> get() = _signUpResultLiveData
    private val _signUpResultLiveData: MutableLiveData<Event<AuthEntity>> = MutableLiveData()

    private val _errorLiveData: MutableLiveData<Event<AppError>> = MutableLiveData()
    val errorLiveData: LiveData<Event<AppError>> = _errorLiveData

    fun signUpViaEmail(email: String, password: String) {
        viewModelScope.launch {
            when (val result = authUseCase.signUpViaEmail(email = email, password = password)) {
                is AppResult.Success -> {
                    _signUpResultLiveData.value = Event(result.data)
                }
                is AppResult.Failure -> {
                    _errorLiveData.value = Event(result.error)
                }
            }
        }
    }
}