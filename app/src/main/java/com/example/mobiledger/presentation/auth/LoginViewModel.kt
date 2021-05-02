package com.example.mobiledger.presentation.auth

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.mobiledger.R
import com.example.mobiledger.common.base.BaseViewModel
import com.example.mobiledger.domain.AppResult
import com.example.mobiledger.domain.entities.UserEntity
import com.example.mobiledger.domain.usecases.AuthUseCase
import com.example.mobiledger.domain.usecases.UserSettingsUseCase
import com.example.mobiledger.presentation.Event
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authUseCase: AuthUseCase,
    private val userSettingsUseCase: UserSettingsUseCase
) : BaseViewModel() {

    val signInResult: LiveData<Event<UserEntity>> get() = _signInResultLiveData
    private val _signInResultLiveData: MutableLiveData<Event<UserEntity>> = MutableLiveData()

    private val _errorLiveData: MutableLiveData<Event<ViewError>> = MutableLiveData()
    val errorLiveData: LiveData<Event<ViewError>> = _errorLiveData

    private val _loadingState = MutableLiveData<Boolean>(false)
    val loadingState: LiveData<Boolean> get() = _loadingState

    fun loginUserViaEmail(email: String, password: String) {
        viewModelScope.launch {
            _loadingState.value = true
            when (val result = authUseCase.loginViaEmail(email, password)) {
                is AppResult.Success -> {
                    saveUIDInCache(result.data.uid)
                    _signInResultLiveData.value = Event(result.data)
                }
                is AppResult.Failure -> {
                    _errorLiveData.value = Event(
                        ViewError(
                            viewErrorType = ViewErrorType.NON_BLOCKING,
                            message = result.error.message
                        )
                    )
                }
            }
            _loadingState.value = false
        }
    }

    fun signInUserViaGoogle(idToken: String?) {
        viewModelScope.launch {
            when (val result = authUseCase.signInViaGoogle(idToken)) {
                is AppResult.Success -> {
                    val a = result.data
                }
                is AppResult.Failure -> {
                    val error = result.error
                }
            }
        }
    }

    private fun saveUIDInCache(uid: String?) {
        viewModelScope.launch {
            userSettingsUseCase.saveUID(uid)
        }
    }

    enum class ViewErrorType { NON_BLOCKING }

    data class ViewError(
        val viewErrorType: ViewErrorType,
        var message: String? = null,
        @StringRes val resID: Int = R.string.generic_error_message
    )
}
