package com.example.mobiledger.presentation.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.mobiledger.common.base.BaseViewModel
import com.example.mobiledger.domain.usecases.UserSettingsUseCase
import com.example.mobiledger.presentation.Event
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashViewModel(private val userSettingsUseCase: UserSettingsUseCase) : BaseViewModel() {

    private val _isUserSignedInLiveData: MutableLiveData<Event<Boolean>> = MutableLiveData()
    val isUserSignedInLiveData: LiveData<Event<Boolean>> = _isUserSignedInLiveData

    private val _isBiometricEnabled = MutableLiveData<Event<Boolean>>()
    val isBiometricIdEnabled: LiveData<Event<Boolean>> get() = _isBiometricEnabled

    private val _isTermsAndConditionAcceptedLiveData = MutableLiveData<Event<Boolean>>()
    val isTermsAndConditionAcceptedLiveData: LiveData<Event<Boolean>> get() = _isTermsAndConditionAcceptedLiveData

    private val splashTimeOut: Long = 2000

    init {
        isTermsAncConditionAccepted()
    }

    private fun isTermsAncConditionAccepted() {
        viewModelScope.launch {
            val tAndCStatus = userSettingsUseCase.isTermsAndConditionAccepted()
            delay(splashTimeOut)
            _isTermsAndConditionAcceptedLiveData.value = Event(tAndCStatus)
        }
    }

    fun isUserSignedIn() {
        viewModelScope.launch {
            val uID = userSettingsUseCase.getUID()
//            delay(splashTimeOut)
            if (uID != null) _isUserSignedInLiveData.value = Event(true)
            else _isUserSignedInLiveData.value = Event(false)
        }
    }

    fun isBiometricEnabled() {
        viewModelScope.launch {
            val isBiometricActive = userSettingsUseCase.isBiometricEnabled()
            _isBiometricEnabled.value = Event(isBiometricActive)
        }
    }
}