package com.example.mobiledger.presentation.budget.updatebudget

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.mobiledger.R
import com.example.mobiledger.common.base.BaseViewModel
import com.example.mobiledger.common.utils.DateUtils.getDateInMMyyyyFormat
import com.example.mobiledger.domain.AppResult
import com.example.mobiledger.domain.usecases.BudgetUseCase
import com.example.mobiledger.presentation.Event
import kotlinx.coroutines.launch
import java.util.*

class UpdateBudgetViewModel(private val budgetUseCase: BudgetUseCase) : BaseViewModel() {

    lateinit var monthYear: Calendar
    lateinit var categoryName: String
    var amount: Long = 0L

    private val _errorLiveData: MutableLiveData<Event<ViewError>> = MutableLiveData()
    val errorLiveData: LiveData<Event<ViewError>> = _errorLiveData

    private val _loadingState = MutableLiveData(false)
    val loadingState: LiveData<Boolean> get() = _loadingState

    val updateResultLiveData: LiveData<Event<Unit>> get() = _updateResultLiveData
    private val _updateResultLiveData: MutableLiveData<Event<Unit>> = MutableLiveData()

    fun updateBudgetAmount(amountChange: Long) {
        viewModelScope.launch {
            _loadingState.value = true
            when (val result = budgetUseCase.updateMonthlyCategoryBudgetAmounts(
                getDateInMMyyyyFormat(monthYear),
                categoryName,
                budgetChange = amountChange
            )) {
                is AppResult.Success -> {
                    _updateResultLiveData.value = Event(Unit)
                }

                is AppResult.Failure -> {
                    if (needToHandleAppError(result.error)) {
                        _errorLiveData.value = Event(
                            ViewError(
                                viewErrorType = ViewErrorType.NON_BLOCKING,
                                message = result.error.message
                            )
                        )
                    }
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