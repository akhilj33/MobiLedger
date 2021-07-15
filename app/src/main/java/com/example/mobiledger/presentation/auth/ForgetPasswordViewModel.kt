package com.example.mobiledger.presentation.auth

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.mobiledger.R
import com.example.mobiledger.common.base.BaseViewModel
import com.example.mobiledger.domain.AppResult
import com.example.mobiledger.domain.usecases.AuthUseCase
import com.example.mobiledger.presentation.Event
import kotlinx.coroutines.launch

class ForgetPasswordViewModel(private val authUseCase: AuthUseCase) : BaseViewModel() {

    private val _loadingState = MutableLiveData<Boolean>(false)
    val loadingState: LiveData<Boolean> get() = _loadingState

    private val _emailSent = MutableLiveData<Unit>()
    val emailSent: LiveData<Unit> get() = _emailSent

    private val _errorLiveData: MutableLiveData<Event<ViewError>> = MutableLiveData()
    val errorLiveData: LiveData<Event<ViewError>> = _errorLiveData

    fun sendEmailToResetPassword(email: String) {
        _loadingState.value = true
        viewModelScope.launch {
            when (val result = authUseCase.sendPasswordResetEmail(email)) {
                is AppResult.Success -> {
                    _emailSent.value = Unit
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
        }
        _loadingState.value = false
    }

    enum class ViewErrorType { NON_BLOCKING }

    data class ViewError(
        val viewErrorType: ViewErrorType,
        var message: String? = null,
        @StringRes val resID: Int = R.string.something_went_wrong
    )
}