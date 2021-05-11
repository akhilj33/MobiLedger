package com.example.mobiledger.common.base

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mobiledger.common.utils.ErrorCodes
import com.example.mobiledger.domain.AppError
import com.example.mobiledger.presentation.Event

abstract class BaseViewModel : ViewModel() {
    protected val _nativeToast = MutableLiveData<Event<NativeToastData>>()
    val nativeToast: LiveData<Event<NativeToastData>> get() = _nativeToast

    private val _authErrorLiveData: MutableLiveData<Event<Unit>> = MutableLiveData()
    val authErrorLiveData: LiveData<Event<Unit>> = _authErrorLiveData

    protected fun needToHandleAppError(appError: AppError): Boolean {
        return when (appError.code) {
            ErrorCodes.HTTP_UNAUTHORIZED,  ErrorCodes.FIREBASE_UNAUTHORIZED-> {
                handleAuthError()
                false
            }
            ErrorCodes.OFFLINE -> false
            else -> true
        }
    }

    private fun handleAuthError() {
        _authErrorLiveData.postValue(Event(Unit))
    }
}

data class NativeToastData(val msg: String? = null, @StringRes val msgRes: Int? = null)
