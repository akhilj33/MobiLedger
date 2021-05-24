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
import com.example.mobiledger.domain.usecases.TransactionUseCase
import com.example.mobiledger.presentation.Event
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*

class AddBudgetDialogViewModel(
    private val budgetUseCase: BudgetUseCase,
    private val transactionUseCase: TransactionUseCase
) : BaseViewModel() {


    private val _errorLiveData: MutableLiveData<Event<ViewError>> = MutableLiveData()
    val errorLiveData: LiveData<Event<ViewError>> = _errorLiveData

    private val _isLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    val dataUpdatedResult: LiveData<Event<Unit>> get() = _dataUpdatedResult
    private val _dataUpdatedResult: MutableLiveData<Event<Unit>> = MutableLiveData()

    private var monthCount = 0
    fun getCurrentMonth(): Calendar = DateUtils.getCurrentDate().apply { add(Calendar.MONTH, monthCount) }


    fun setBudget(monthlyBudgetData: MonthlyBudgetData, month: String) {
        _isLoading.value = true
        viewModelScope.launch {
            val setBudgetJob =
                async { budgetUseCase.setMonthlyBudget(month, monthlyBudgetData) }

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
        _isLoading.value = false
    }

    //   ------------------- Add new category budget -------------------
    fun getMonthlyCategorySummary(category: String, amt: Long, month: String, budgetTotal: Long) {
        _isLoading.value = true
        viewModelScope.launch {
            when (val result = transactionUseCase.getMonthlyCategorySummary(DateUtils.getDateInMMyyyyFormat(getCurrentMonth()), category)) {
                is AppResult.Success -> {
                    if (result.data != null) {
                        addCategoryBudgetToFirebase(category, amt, result.data.totalCategoryExpense, month, budgetTotal)
                    } else
                        addCategoryBudgetToFirebase(category, amt, 0, month, budgetTotal)
                }
                is AppResult.Failure -> {
                    addCategoryBudgetToFirebase(category, amt, 0, month, budgetTotal)
                    _errorLiveData.value = Event(
                        ViewError(
                            viewErrorType = ViewErrorType.NON_BLOCKING,
                            message = result.error.message
                        )
                    )
                    _isLoading.value = false
                }
            }
        }

    }


    private fun addCategoryBudgetToFirebase(category: String, amt: Long, categoryExpense: Long, month: String, budgetTotal: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            when (val result = budgetUseCase.addCategoryBudget(
                month,
                MonthlyCategoryBudget(category, amt, categoryExpense)
            )) {
                is AppResult.Success -> {
                    Timber.i("Category Budget Added")
                    updateTotalBudget(amt, month, budgetTotal)
                }

                is AppResult.Failure -> {
                    _errorLiveData.value = Event(
                        ViewError(
                            viewErrorType = ViewErrorType.NON_BLOCKING,
                            message = result.error.message
                        )
                    )
                    _isLoading.value = false
                }
            }
        }
    }


    private fun updateTotalBudget(amt: Long, month: String, budgetTotal: Long) {
        _isLoading.value = true
        viewModelScope.launch {
            when (val result = budgetUseCase.updateBudgetTotal(month, amt + budgetTotal)) {
                is AppResult.Success -> {
                    Timber.i("Total Budget updated")
                    _dataUpdatedResult.value = Event(result.data)
                }
                is AppResult.Failure -> {
                    _errorLiveData.value = Event(
                        ViewError(
                            viewErrorType = ViewErrorType.NON_BLOCKING,
                            message = result.error.message
                        )
                    )
                    _isLoading.value = false
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