package com.example.mobiledger.presentation.budget

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.mobiledger.R
import com.example.mobiledger.common.base.BaseViewModel
import com.example.mobiledger.common.utils.DateUtils
import com.example.mobiledger.domain.AppResult
import com.example.mobiledger.domain.usecases.BudgetUseCase
import com.example.mobiledger.presentation.Event
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*

class AddBudgetDialogViewModel(private val budgetUseCase: BudgetUseCase) : BaseViewModel() {

    private val _isLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    val dataUpdatedResult: LiveData<Event<Unit>> get() = _dataUpdatedResult
    private val _dataUpdatedResult: MutableLiveData<Event<Unit>> = MutableLiveData()

    private var monthCount = 0
    private fun getCurrentMonth(): Calendar = DateUtils.getCurrentDate().apply { add(Calendar.MONTH, monthCount) }

    fun setBudget(monthlyBudgetData: MonthlyBudgetData) {
        viewModelScope.launch {
            val setBudgetJob =
                async { budgetUseCase.setMonthlyBudget(DateUtils.getDateInMMyyyyFormat(getCurrentMonth()), monthlyBudgetData) }

            when (val result = setBudgetJob.await()) {
                is AppResult.Success -> {
                    Timber.i("Budget Set")
                    _dataUpdatedResult.value = Event(result.data)
                }

                is AppResult.Failure -> {
                    Event(
                        ViewError(
                            viewErrorType = ViewErrorType.NON_BLOCKING,
                            message = result.error.message
                        )
                    )
                }
            }
        }
    }

    enum class ViewErrorType { NON_BLOCKING }

    data class ViewError(
        val viewErrorType: ViewErrorType,
        var message: String? = null,
        @StringRes val resID: Int = R.string.generic_error_message
    )
}