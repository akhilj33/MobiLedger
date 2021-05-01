package com.example.mobiledger.presentation.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.mobiledger.common.base.BaseViewModel
import com.example.mobiledger.common.utils.ErrorCodes
import com.example.mobiledger.domain.AppError
import com.example.mobiledger.domain.AppResult
import com.example.mobiledger.domain.entities.UserInfoEntity
import com.example.mobiledger.domain.usecases.ProfileUseCase
import com.example.mobiledger.domain.usecases.UserSettingsUseCase
import com.example.mobiledger.presentation.Event
import kotlinx.coroutines.launch

class EditProfileViewModel(
    private val userSettingsUseCase: UserSettingsUseCase,
    private val profileUseCase: ProfileUseCase
) : BaseViewModel() {

    val userFromFirestoreResult: LiveData<Event<UserInfoEntity>> get() = _userFromFirestoreResult
    private val _userFromFirestoreResult: MutableLiveData<Event<UserInfoEntity>> = MutableLiveData()

    val dataUpdatedResult: LiveData<Event<Unit>> get() = _dataUpdatedResult
    private val _dataUpdatedResult: MutableLiveData<Event<Unit>> = MutableLiveData()

    private val _errorLiveData: MutableLiveData<Event<AppError>> = MutableLiveData()
    val errorLiveData: LiveData<Event<AppError>> = _errorLiveData

    init {
        getUIDForProfile()
    }

    fun getUIDForProfile() {
        viewModelScope.launch {
            val uid = userSettingsUseCase.getUID()
            fetchUserData(uid!!)
        }
    }

    private fun fetchUserData(uid: String) {
        viewModelScope.launch {
            when (val result = profileUseCase.fetchUserFromFirestoreDb(uid)) {
                is AppResult.Success -> {
                    _userFromFirestoreResult.value = Event(result.data!!)
                }
                is AppResult.Failure -> {
                    _errorLiveData.value = Event(result.error)
                }
            }
        }
    }

    fun updateUserName(userName: String) {
        viewModelScope.launch {
            val uid = userSettingsUseCase.getUID()
            if (uid != null) {
                when (val result = profileUseCase.updateUserNameInFirebase(userName, uid)) {
                    is AppResult.Success -> {
                        _dataUpdatedResult.value = Event(result.data)
                    }
                    is AppResult.Failure -> {
                        _errorLiveData.value = Event(result.error)
                    }
                }
            } else _errorLiveData.value = Event(AppError(ErrorCodes.GENERIC_ERROR))
        }
    }

    fun updateEmail(email: String) {
        viewModelScope.launch {
            val uid = userSettingsUseCase.getUID()
            if (uid != null) {
                when (val result = profileUseCase.updateEmailInFirebase(email, uid)) {
                    is AppResult.Success -> {
                        _dataUpdatedResult.value = Event(result.data)
                    }
                    is AppResult.Failure -> {
                        _errorLiveData.value = Event(result.error)
                    }
                }
            } else _errorLiveData.value = Event(AppError(ErrorCodes.GENERIC_ERROR))
        }
    }

    fun updatePhoneNo(phone: String) {
        viewModelScope.launch {
            val uid = userSettingsUseCase.getUID()
            if (uid != null) {
                when (val result = profileUseCase.updatePhoneInFirebaseDB(phone, uid)) {
                    is AppResult.Success -> {
                        _dataUpdatedResult.value = Event(result.data)
                    }
                    is AppResult.Failure -> {
                        _errorLiveData.value = Event(result.error)
                    }
                }
            } else _errorLiveData.value = Event(AppError(ErrorCodes.GENERIC_ERROR))
        }
    }

    fun updatePassword(password: String) {
        viewModelScope.launch {
            when (val result = profileUseCase.updatePasswordInFirebase(password)) {
                is AppResult.Success -> {
                    _dataUpdatedResult.value = Event(result.data)
                }
                is AppResult.Failure -> {
                    _errorLiveData.value = Event(result.error)
                }
            }
        }
    }
}
