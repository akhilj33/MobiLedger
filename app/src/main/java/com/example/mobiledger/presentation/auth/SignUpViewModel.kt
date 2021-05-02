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
import com.example.mobiledger.domain.usecases.UserUseCase
import com.example.mobiledger.presentation.Event
import kotlinx.coroutines.launch

class SignUpViewModel(private val authUseCase: AuthUseCase, private val userUseCase: UserUseCase,
                      private val userSettingsUseCase: UserSettingsUseCase
) : BaseViewModel() {

    val signUpResult: LiveData<Event<UserEntity>> get() = _signUpResultLiveData
    private val _signUpResultLiveData: MutableLiveData<Event<UserEntity>> = MutableLiveData()

    private val _errorLiveData: MutableLiveData<Event<ViewError>> = MutableLiveData()
    val errorLiveData: LiveData<Event<ViewError>> = _errorLiveData

    private val _loadingState = MutableLiveData<Boolean>(false)
    val loadingState: LiveData<Boolean> get() = _loadingState

    fun signUpViaEmail(name: String, phoneNo: String, email: String, password: String) {
        viewModelScope.launch {
            _loadingState.value = true
            when (val result = authUseCase.signUpViaEmail(name, phoneNo, email, password)) {
                is AppResult.Success -> {
                    addUserToFirebaseDB(result.data)
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

    //todo how to handle case when user is successfully signed up by isn't added to db
    private fun addUserToFirebaseDB(user: UserEntity){
        viewModelScope.launch {
            when(val result = userUseCase.addUserToFirebaseDb(user)){
                is AppResult.Success -> {
                    saveUIDInCache(user.uid)
                    _signUpResultLiveData.value = Event(user)
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

    private fun saveUIDInCache(uid: String) {
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