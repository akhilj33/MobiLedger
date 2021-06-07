package com.example.mobiledger.presentation.profile

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.mobiledger.R
import com.example.mobiledger.common.base.BaseViewModel
import com.example.mobiledger.domain.AppResult
import com.example.mobiledger.domain.entities.UserEntity
import com.example.mobiledger.domain.usecases.ProfileUseCase
import com.example.mobiledger.domain.usecases.UserSettingsUseCase
import com.example.mobiledger.presentation.Event
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val profileUseCase: ProfileUseCase,
    private val userSettingsUseCase: UserSettingsUseCase
) : BaseViewModel() {

    val userFromFirestoreResult: LiveData<UserEntity> get() = _userFromFirestoreResult
    private val _userFromFirestoreResult: MutableLiveData<UserEntity> = MutableLiveData()

    private val _errorLiveData: MutableLiveData<Event<ViewError>> = MutableLiveData()
    val errorLiveData: LiveData<Event<ViewError>> = _errorLiveData

    private val _loadingState = MutableLiveData(false)
    val loadingState: LiveData<Boolean> get() = _loadingState

    private val _isBiometricEnabled = MutableLiveData<Event<Boolean>>()
    val isBiometricIdEnabled: LiveData<Event<Boolean>> get() = _isBiometricEnabled

    private val _isPushNotificationEnabled = MutableLiveData<Event<Boolean>>()
    val isPushNotificationEnabled: LiveData<Event<Boolean>> get() = _isPushNotificationEnabled

    private val _isReminderEnabled = MutableLiveData<Event<Boolean>>()
    val isReminderEnabled: LiveData<Event<Boolean>> get() = _isReminderEnabled


    fun fetchUserData() {
        viewModelScope.launch {
            _loadingState.value = true
            when (val result = profileUseCase.fetchUserFromFirebase()) {
                is AppResult.Success -> {
                    _userFromFirestoreResult.value = result.data
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

    fun isPushNotificationEnabled() {
        viewModelScope.launch {
            val isPushNotificationEnabled = userSettingsUseCase.isNotificationEnabled()
            _isPushNotificationEnabled.value = Event(isPushNotificationEnabled)
        }

    }

    fun savePushNotificationEnabled(enablePushNotification: Boolean) {
        viewModelScope.launch {
            userSettingsUseCase.saveNotificationEnabled(enablePushNotification)
            _loadingState.value = false
        }
    }

    fun isReminderEnabled() {
        viewModelScope.launch {
            val isReminderNotificationEnabled = userSettingsUseCase.isReminderEnabled()
            _isReminderEnabled.value = Event(isReminderNotificationEnabled)
        }

    }

    fun saveReminderEnabled(enableReminder: Boolean) {
        viewModelScope.launch {
            userSettingsUseCase.saveReminderEnabled(enableReminder)
            _loadingState.value = false
        }
    }

    fun isBiometricEnabled() {
        viewModelScope.launch {
            val isBiometricEnabled = userSettingsUseCase.isBiometricEnabled()
            _isBiometricEnabled.value = Event(isBiometricEnabled)
        }

    }

    fun saveBiometricEnabled(enableBiometric: Boolean) {
        viewModelScope.launch {
            userSettingsUseCase.saveBiometricEnabled(enableBiometric)
        }
    }

    enum class ViewErrorType { NON_BLOCKING }

    data class ViewError(
        val viewErrorType: ViewErrorType,
        var message: String? = null,
        @StringRes val resID: Int = R.string.generic_error_message
    )
}