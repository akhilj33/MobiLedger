package com.example.mobiledger.presentation.onBoarding

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.mobiledger.R
import com.example.mobiledger.common.base.BaseViewModel
import com.example.mobiledger.domain.usecases.UserSettingsUseCase
import com.example.mobiledger.presentation.Event
import kotlinx.coroutines.launch

class TermsAndConditionViewModel(
    private val userSettingsUseCase: UserSettingsUseCase
) : BaseViewModel() {

    private val _isLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorLiveData: MutableLiveData<Event<ViewError>> = MutableLiveData()
    val errorLiveData: LiveData<Event<ViewError>> = _errorLiveData

    fun acceptTermsAndCondition() {
        viewModelScope.launch {
            userSettingsUseCase.acceptTermsAndCondition(true)
        }
    }

    enum class ViewErrorType { NON_BLOCKING }

    data class ViewError(
        val viewErrorType: ViewErrorType,
        var message: String? = null,
        @StringRes val resID: Int = R.string.generic_error_message
    )
}
