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

class ProfileViewModel(
    private val profileUseCase: ProfileUseCase
) : BaseViewModel() {

    val userFromFirestoreResult: LiveData<UserInfoEntity> get() = _userFromFirestoreResult
    private val _userFromFirestoreResult: MutableLiveData<UserInfoEntity> = MutableLiveData()

    private val _errorLiveData: MutableLiveData<Event<AppError>> = MutableLiveData()
    val errorLiveData: LiveData<Event<AppError>> = _errorLiveData

    fun fetchUserData() {
        viewModelScope.launch {
            when (val result = profileUseCase.fetchUserFromFirebase()) {
                is AppResult.Success -> {
                    _userFromFirestoreResult.value = result.data
                }
                is AppResult.Failure -> {
                    _errorLiveData.value = Event(result.error)
                }
            }
        }
    }
}