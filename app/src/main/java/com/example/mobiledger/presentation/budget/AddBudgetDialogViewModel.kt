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
import com.example.mobiledger.domain.usecases.CategoryUseCase
import com.example.mobiledger.domain.usecases.TransactionUseCase
import com.example.mobiledger.presentation.Event
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*

class AddBudgetDialogViewModel(
    private val budgetUseCase: BudgetUseCase,
    private val categoryUseCase: CategoryUseCase
) : BaseViewModel() {


    private val _errorLiveData: MutableLiveData<Event<ViewError>> = MutableLiveData()
    val errorLiveData: LiveData<Event<ViewError>> = _errorLiveData

    private val _isLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    val dataUpdatedResult: LiveData<Event<Unit>> get() = _dataUpdatedResult
    private val _dataUpdatedResult: MutableLiveData<Event<Unit>> = MutableLiveData()

    lateinit var expenseCategoryList: ArrayList<String>
    lateinit var month: String
    var budgetTotal: Long= 0L
    var isCategoryBudget: Boolean = true

    fun setBudget(monthlyBudgetData: MonthlyBudgetData) {
        _isLoading.value = true
        viewModelScope.launch {
            val setBudgetJob =
                async { budgetUseCase.setMonthlyBudget(month, monthlyBudgetData) }

            when (val result = setBudgetJob.await()) {
                is AppResult.Success -> {
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
    fun getMonthlyCategorySummary(category: String, amt: Long) {
        _isLoading.value = true
        viewModelScope.launch {
            when (val result = categoryUseCase.getMonthlyCategorySummary(month, category)) {
                is AppResult.Success -> {
                   val categoryExpense =  if (result.data != null) result.data.categoryAmount else 0L
                    addCategoryBudgetToFirebase(category, amt, categoryExpense, month, budgetTotal)
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
            when (val result = budgetUseCase.addCategoryBudget(month, MonthlyCategoryBudget(category, amt, categoryExpense)
            )) {
                is AppResult.Success -> {
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