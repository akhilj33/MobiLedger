package com.example.mobiledger.presentation.budgetTemplate

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.mobiledger.R
import com.example.mobiledger.common.base.BaseViewModel
import com.example.mobiledger.domain.AppResult
import com.example.mobiledger.domain.entities.NewBudgetTemplateEntity
import com.example.mobiledger.domain.usecases.BudgetTemplateUseCase
import com.example.mobiledger.presentation.Event
import com.google.firebase.Timestamp
import kotlinx.coroutines.launch

class AddBudgetTemplateViewModel(
    private val budgetTemplateUseCase: BudgetTemplateUseCase
) : BaseViewModel() {

    private val _loadingState = MutableLiveData<Boolean>(false)
    val loadingState: LiveData<Boolean> get() = _loadingState

    private val _dataAdded = MutableLiveData<Boolean>(false)
    val dataAdded: LiveData<Boolean> get() = _dataAdded

    private val _errorLiveData: MutableLiveData<Event<ViewError>> = MutableLiveData()
    val errorLiveData: LiveData<Event<ViewError>> =_errorLiveData

    var templateList: MutableList<NewBudgetTemplateEntity> = mutableListOf()

    fun addNewBudgetTemplate(name: String, maxLimitAmount: Long) {
        viewModelScope.launch {
            _loadingState.value = true
            when (val result = budgetTemplateUseCase.addNewBudgetTemplate(
                NewBudgetTemplateEntity(name, maxLimitAmount, Timestamp.now())
            )) {
                is AppResult.Success -> {
                    _loadingState.value = false
                    _dataAdded.value = true
                }

                is AppResult.Failure -> {
                    _errorLiveData.value = Event(
                        ViewError(
                            viewErrorType = ViewErrorType.NON_BLOCKING,
                            message = result.error.message
                        )
                    )
                    _loadingState.value = false
                }
            }
        }
    }

    enum class ViewErrorType { NON_BLOCKING }

    data class ViewError(
        val viewErrorType: ViewErrorType,
        var message: String? = null,
        @StringRes val resID: Int = R.string.something_went_wrong
    )
}

