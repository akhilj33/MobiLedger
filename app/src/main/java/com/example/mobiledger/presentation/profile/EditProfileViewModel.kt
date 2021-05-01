package com.example.mobiledger.presentation.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.mobiledger.common.base.BaseViewModel
import com.example.mobiledger.domain.AppError
import com.example.mobiledger.domain.AppResult
import com.example.mobiledger.domain.entities.UserInfoEntity
import com.example.mobiledger.domain.usecases.ProfileUseCase
import com.example.mobiledger.presentation.Event
import kotlinx.coroutines.launch

class EditProfileViewModel(private val profileUseCase: ProfileUseCase) : BaseViewModel() {

    val userFromFirebaseResult: LiveData<Event<UserInfoEntity?>> get() = _userFromFirebaseResult
    private val _userFromFirebaseResult: MutableLiveData<Event<UserInfoEntity?>> = MutableLiveData()

    val dataUpdatedResult: LiveData<Event<Unit>> get() = _dataUpdatedResult
    private val _dataUpdatedResult: MutableLiveData<Event<Unit>> = MutableLiveData()

    private val _errorLiveData: MutableLiveData<Event<AppError>> = MutableLiveData()
    val errorLiveData: LiveData<Event<AppError>> = _errorLiveData

    fun fetchUserData() {
        viewModelScope.launch {
            when (val result = profileUseCase.fetchUserFromFirebase()) {
                is AppResult.Success -> {
                    _userFromFirebaseResult.value = Event(result.data)
                }
                is AppResult.Failure -> {
                    _errorLiveData.value = Event(result.error)
                }
            }
        }
    }

    fun updateUserName(userName: String) {
        viewModelScope.launch {
            when (val result = profileUseCase.updateUserNameInFirebase(userName)) {
                is AppResult.Success -> {
                    _dataUpdatedResult.value = Event(result.data)
                }
                is AppResult.Failure -> {
                    _errorLiveData.value = Event(result.error)
                }
            }
        }
    }

    fun updateEmail(email: String) {
        viewModelScope.launch {
            when (val result = profileUseCase.updateEmailInFirebase(email)) {
                is AppResult.Success -> {
                    _dataUpdatedResult.value = Event(result.data)
                }
                is AppResult.Failure -> {
                    _errorLiveData.value = Event(result.error)
                }
            }
        }
    }

    fun updatePhoneNo(phone: String) {
        viewModelScope.launch {
            when (val result = profileUseCase.updatePhoneInFirebase(phone)) {
                is AppResult.Success -> {
                    _dataUpdatedResult.value = Event(result.data)
                }
                is AppResult.Failure -> {
                    _errorLiveData.value = Event(result.error)
                }
            }
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
