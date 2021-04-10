package com.example.mobiledger.presentation.profile

import androidx.lifecycle.viewModelScope
import com.example.mobiledger.common.base.BaseViewModel
import com.example.mobiledger.domain.usecases.UserSettingsUseCase
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val userSettingsUseCase: UserSettingsUseCase
) : BaseViewModel() {

    init {
        getUIDForProfile()

    }

    fun getUIDForProfile() {
        viewModelScope.launch {
            val uid = userSettingsUseCase.getUID()
        }
    }

    fun fetchUserData(uid: String?) {

    }
}