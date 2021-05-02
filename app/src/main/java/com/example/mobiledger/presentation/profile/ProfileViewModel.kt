package com.example.mobiledger.presentation.profile

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.mobiledger.R
import com.example.mobiledger.common.base.BaseViewModel
import com.example.mobiledger.domain.AppResult
import com.example.mobiledger.domain.entities.UserInfoEntity
import com.example.mobiledger.domain.usecases.ProfileUseCase
import com.example.mobiledger.domain.usecases.UserSettingsUseCase
import com.example.mobiledger.presentation.Event
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val userSettingsUseCase: UserSettingsUseCase,
    private val profileUseCase: ProfileUseCase
) : BaseViewModel() {

    val userFromFirestoreResult: LiveData<Event<UserInfoEntity>> get() = _userFromFirestoreResult
    private val _userFromFirestoreResult: MutableLiveData<Event<UserInfoEntity>> = MutableLiveData()

    private val _errorLiveData: MutableLiveData<Event<ViewError>> = MutableLiveData()
    val errorLiveData: LiveData<Event<ViewError>> = _errorLiveData

    private val _loadingState = MutableLiveData<Boolean>(false)
    val loadingState: LiveData<Boolean> get() = _loadingState

    init {
        getUIDForProfile()
    }

    fun getUIDForProfile() {
        viewModelScope.launch {
            _loadingState.value = true
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

    enum class ViewErrorType { NON_BLOCKING }

    data class ViewError(
        val viewErrorType: ViewErrorType,
        var message: String? = null,
        @StringRes val resID: Int = R.string.generic_error_message
    )
}